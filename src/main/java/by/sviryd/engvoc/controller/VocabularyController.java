package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.service.MessageI18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
@RequestMapping("vocabulary")
public class VocabularyController {
    @Autowired
    private MessageI18nService messageI18nService;

    @GetMapping()
    public String vocabulary(
            Model model,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("titleVocabulary", new Object[]{}, locale));
        return "main";
    }
}
