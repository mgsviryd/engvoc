package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.service.MessageI18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class MainController {
    @Autowired
    private MessageI18nService messageI18nService;

    @GetMapping("/")
    public String up(Model model, Locale locale) {
        model.addAttribute("title", messageI18nService.getMessage("titleMain", new Object[]{}, locale));
        return "main";
    }
}
