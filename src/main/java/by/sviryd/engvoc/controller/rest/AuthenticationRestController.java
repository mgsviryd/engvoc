package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.gson.GsonExcludeStrategies;
import by.sviryd.engvoc.service.LocaleExceptionMessageService;
import by.sviryd.engvoc.service.RegisterUserService;
import by.sviryd.engvoc.service.UserService;
import by.sviryd.engvoc.service.VerificationTokenSenderService;
import by.sviryd.engvoc.type.LangLocale;
import by.sviryd.engvoc.util.LocaleException;
import by.sviryd.engvoc.util.LocaleExceptionMessage;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/json/authentication")
public class AuthenticationRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenSenderService verificationTokenSenderService;
    @Autowired
    private GsonExcludeStrategies gsonExcludeStrategies;
    @Autowired
    private RegisterUserService registerUserService;
    @Autowired
    private LocaleExceptionMessageService localeExceptionMessageService;

    @PostMapping()
    public String getUsers(Principal principal, @AuthenticationPrincipal User user, @RequestBody String json) {
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

    @PostMapping("/register")
    public HashMap<Object, Object> register(@RequestBody String json) {
        Gson gson = new Gson();
        HashMap<Object, Object> frontend = new HashMap<>();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String email = obj.get("email").getAsString();
        String password = obj.get("password").getAsString();
        String passwordRepeat = obj.get("passwordRepeat").getAsString();
        String recaptchaResponse = obj.get("recaptchaResponse").getAsString();
        JsonObject langJson = obj.get("lang").getAsJsonObject();
        Type langLocaleType = new TypeToken<LangLocale>(){}.getType();
        LangLocale langLocale = gson.fromJson(langJson, langLocaleType);
        Locale locale = langLocale.getLocale();
        List<LocaleException> exs = registerUserService.validate(email, password, passwordRepeat, recaptchaResponse);
        if (exs.isEmpty()) {
            User user = registerUserService.register(email, password);
            verificationTokenSenderService.sendVerificationToken(user, locale);
        }
        List<LocaleExceptionMessage> lems = exs.stream().map(e -> localeExceptionMessageService.getLEM(e, locale)).collect(Collectors.toList());
        frontend.put("errors", lems);
        return frontend;
    }
}
