package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.service.MessageI18nService;
import by.sviryd.engvoc.service.UserAuthenticationService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;

@Controller
public class MainController {
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping("/")
    public String main(
            Model model,
            Authentication authentication) {
        HashMap<Object, Object> data = new HashMap<>();
        User user = null;
        if (authentication != null){
            user = userAuthenticationService.getUser(authentication);
        }
        data.put("user", user);
        Gson gson = new Gson();
        String json = gson.toJson(data);
        model.addAttribute("data", json);
        return "main";
    }
}
