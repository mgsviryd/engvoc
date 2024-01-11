package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.FrontendConfig;
import by.sviryd.engvoc.config.LocaleConfig;
import by.sviryd.engvoc.config.PictureMediaConfig;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.service.MessageI18nService;
import by.sviryd.engvoc.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/json/frontend")
public class FrontendRestController {
    @Autowired
    private FrontendConfig frontendConfig;
    @Autowired
    private LocaleConfig langConfig;
    @Autowired
    private PictureMediaConfig pictureMediaConfig;
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private UserService userService;

    @PostMapping()
    @JsonView({Views.UserAndLangLocaleAndPictureMediaDTO.class})
    public Map<Object, Object> getFrontend(
            Principal principal,
            @AuthenticationPrincipal User user,
            @RequestBody String json,
            @RequestParam String lang,
            Locale locale
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray tokensArray = obj.get("tokens").getAsJsonArray();
        Type stringType = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> tokens = gson.fromJson(tokensArray, stringType);
        HashMap<Object, Object> data = new HashMap<>();
        List<User> users;
        if (user != null) {
            data.put("user", user);
        } else {
            try {
                OAuth2Authentication auth2 = (OAuth2Authentication) principal;
                String sub = ((HashMap<String, String>) (auth2.getUserAuthentication().getDetails())).get("sub");
                user = userService.findBySub(sub);
                data.put("user", user);
            } catch (Exception e) {
                data.put("user", null);
            }
        }
        if (tokens.isEmpty()) {
            users = new ArrayList<>(1);
        } else {
            users = IterableUtils.toList(userService.findAllByTokenIn(tokens));
        }
        if (user != null) {
            Long id = user.getId();
            if (users.stream().noneMatch(u -> u.getId().equals(id))) {
                users.add(user);
            }
        }
        data.put("users", users);
        data.put("config", frontendConfig.getConfig());
        data.put("version", frontendConfig.getVersion());
        data.put("langLangs", langConfig.getLangLocales());
        data.put("langMap", messageI18nService.getMessages(locale));
        data.put("pictureMedias", pictureMediaConfig.getPictureMedias());
        data.put("defaultPictureFileName", pictureMediaConfig.getDefaultPictureFileName());
        return data;
    }
}
