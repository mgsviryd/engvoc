package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.LangConfig;
import by.sviryd.engvoc.service.MessageSourceOnlyLanguageService;
import by.sviryd.engvoc.type.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/json/lang")
public class LangRestController {
    @Autowired
    private LangConfig languageConfig;
    @Autowired
    private MessageSourceOnlyLanguageService messageSourceOnlyLanguageService;

    @GetMapping("/list")
    public List<Lang> getLangs() {
        return languageConfig.getLangs();
    }

    @GetMapping("/map")
    public Map<String, String> getMap(
            @RequestParam String lang
    ) {
        return messageSourceOnlyLanguageService.getMessages(new Locale(lang));
    }

    @GetMapping("/change")
    public void changeLang(
            @RequestParam String lang) {
    }
}
