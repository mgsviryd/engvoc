package by.sviryd.engvoc.controller;

import by.sviryd.engvoc.service.MessageI18nService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("card")
    public String card(
            Model model,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("titleVocabulary", new Object[]{}, locale));
        return "main";
    }
    @GetMapping("dictionary")
    public String dictionary(
            Model model,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("titleVocabulary", new Object[]{}, locale));
        return "main";
    }
    @GetMapping("editor")
    public String editor(
            Model model,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("titleVocabulary", new Object[]{}, locale));
        return "main";
    }
    @GetMapping("/editor/:idDictionary1/:idDictionary2")
    public String editorDictionaries(
            Model model,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("titleVocabulary", new Object[]{}, locale));
        return "main";
    }
    @GetMapping("origin")
    public String origin(
            Model model,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("titleVocabulary", new Object[]{}, locale));
        return "main";
    }
    @GetMapping("settings")
    public String settings(
            Model model,
            Locale locale
    ) {
        model.addAttribute("title", messageI18nService.getMessage("titleVocabulary", new Object[]{}, locale));
        return "main";
    }

}
