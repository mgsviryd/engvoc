package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.LocaleConfig;
import by.sviryd.engvoc.service.MessageI18nService;
import by.sviryd.engvoc.type.LangLocale;
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
public class LocaleRestController {
    @Autowired
    private LocaleConfig languageConfig;
    @Autowired
    private MessageI18nService messageI18nService;

    @GetMapping("/list")
    public List<LangLocale> getLangs() {
        return languageConfig.getLangLocales();
    }

    @GetMapping("/map")
    public Map<String, String> getMap(
            @RequestParam String lang
    ) {
        return messageI18nService.getMessages(LangLocale.getLangLocale(lang).getLocale());
    }

    @GetMapping("/change")
    public Locale changeLang(
            @RequestParam String lang,
            Locale locale) {
        return locale;
    }
}
