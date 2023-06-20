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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;

@Controller
public class MainController {
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping("/")
    public String main(
            Model model,
            Authentication authentication,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("logo", new Object[]{}, locale));
        HashMap<Object, Object> data = new HashMap<>();
        User user = null;
        if (authentication != null) {
            user = userAuthenticationService.getUser(authentication);
        }
        data.put("user", user);
        Gson gson = new Gson();
        String json = gson.toJson(data);
        model.addAttribute("data", json);
        return "main";
    }
}
