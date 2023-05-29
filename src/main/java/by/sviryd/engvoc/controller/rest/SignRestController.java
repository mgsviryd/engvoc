package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.gson.GsonExcludeStrategies;
import by.sviryd.engvoc.service.MessageI18nService;
import by.sviryd.engvoc.util.LocaleExceptionMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

@RestController
@RequestMapping("/json/sign")
public class SignRestController {
    @Autowired
    private GsonExcludeStrategies gsonExcludeStrategies;
    @Autowired
    private MessageI18nService messageI18nService;

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
}
