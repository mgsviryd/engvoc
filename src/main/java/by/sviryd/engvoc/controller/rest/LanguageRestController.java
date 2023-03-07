package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.LanguageConfig;
import by.sviryd.engvoc.domain.LanguageDTO;
import by.sviryd.engvoc.service.MessageSourceOnlyLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("json/lang")
public class LanguageRestController {
    @Autowired
    private LanguageConfig languageConfig;
    @Autowired
    private MessageSourceOnlyLanguageService messageSourceOnlyLanguageService;

    @GetMapping("list")
    public List<LanguageDTO> getLanguages() {
        return languageConfig.getLanguages();
    }
    @GetMapping(value = "map/{lang}")
    public Map<String,String> getMap(
            @PathVariable String lang
    ) {
        return messageSourceOnlyLanguageService.getMessages(new Locale(lang));
    }
}
