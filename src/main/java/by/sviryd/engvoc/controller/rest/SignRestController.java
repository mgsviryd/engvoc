package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.gson.GsonExcludeStrategies;
import by.sviryd.engvoc.service.*;
import by.sviryd.engvoc.util.LocaleException;
import by.sviryd.engvoc.util.LocaleExceptionMessage;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/json/sign")
public class SignRestController {
    @Autowired
    private GsonExcludeStrategies gsonExcludeStrategies;
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private VerificationTokenSenderService verificationTokenSenderService;
    @Autowired
    private UserService userService;
    @Autowired
    private SignUpUserService signUpUserService;
    @Autowired
    private LocaleExceptionMessageService localeExceptionMessageService;

    @PostMapping(value = "/failure")
    public HashMap<Object, Object> failure(
            Locale locale
    ) {
        String code = "signInFailure";
        String message = messageI18nService.getMessage(code, null, locale);
        HashMap<Object, Object> data = new HashMap<>();
        LocaleExceptionMessage error = new LocaleExceptionMessage(code, "password", message);
        data.put("errors", Collections.singletonList(error));
        return data;
    }

    @PostMapping(value = "/success")
    public String success(
            @AuthenticationPrincipal User user
    ) {
        Gson gson = new Gson();
        HashMap<Object, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("errors", Collections.emptyList());
        gson = new GsonBuilder().addSerializationExclusionStrategy(gsonExcludeStrategies.getUserOnlyInfo()).create();
        return gson.toJson(data);
    }

    @GetMapping("/sendVerificationToken")
    public boolean sendVerificationToken(@AuthenticationPrincipal User user, Locale locale) {
        verificationTokenSenderService.sendSignUpMailConfirmation("/sign/activate", user, locale);
        return true;
    }

    @PostMapping("/users")
    public String getUsers(Principal principal,
                           @AuthenticationPrincipal User user,
                           @RequestBody String json
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray tokensArray = obj.get("tokens").getAsJsonArray();
        Type stringType = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> tokens = gson.fromJson(tokensArray, stringType);
        Map<String, Object> frontendData = new HashMap<>();
        Iterable<User> users;
        if (tokens.isEmpty()) {
            users = Collections.emptyList();
        } else {
            users = userService.getUsersByTokens(tokens);
        }
        frontendData.put("users", users);
        if (user != null) {
            frontendData.put("user", user);
        } else {
            try {
                OAuth2Authentication auth2 = (OAuth2Authentication) principal;
                String sub = ((HashMap<String, String>) (auth2.getUserAuthentication().getDetails())).get("sub");
                user = userService.findBySub(sub);
                frontendData.put("user", user);
            } catch (Exception e) {
            }
        }
        gson = new GsonBuilder().addSerializationExclusionStrategy(gsonExcludeStrategies.getUserOnlyInfo()).create();
        return gson.toJson(frontendData);
    }

    @PostMapping("/up")
    public HashMap<Object, Object> up(@RequestBody String json, Locale locale) {
        Gson gson = new Gson();
        HashMap<Object, Object> data = new HashMap<>();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String email = obj.get("email").getAsString();
        String password = obj.get("password").getAsString();
        String passwordRepeat = obj.get("passwordRepeat").getAsString();
        String recaptchaResponse = obj.get("recaptchaResponse").getAsString();
        List<LocaleException> exs = signUpUserService.validate(email, password, passwordRepeat, recaptchaResponse);
        if (exs.isEmpty()) {
            User user = signUpUserService.up(email, password);
            verificationTokenSenderService.sendSignUpMailConfirmation("/sign/activate", user, locale);
        }
        List<LocaleExceptionMessage> lems = exs.stream().map(e -> localeExceptionMessageService.getLEM(e, locale)).collect(Collectors.toList());
        data.put("errors", lems);
        return data;
    }
}
