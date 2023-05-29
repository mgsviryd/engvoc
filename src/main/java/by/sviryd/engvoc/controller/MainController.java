package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.service.MessageI18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Autowired
    private MessageI18nService messageI18nService;

    @GetMapping("/")
    public String main(Authentication authentication) {
        return "main";
    }

    @GetMapping("/notFound")
    public String notFound(Authentication authentication) {
        return "main";
    }
}
