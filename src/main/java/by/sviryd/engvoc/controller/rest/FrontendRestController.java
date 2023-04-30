package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.FrontendConfig;
import by.sviryd.engvoc.config.LangConfig;
import by.sviryd.engvoc.service.MessageSourceOnlyLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/json/frontend")
public class FrontendRestController {
    @Autowired
    private FrontendConfig frontendConfig;
    @Autowired
    private LangConfig langConfig;
    @Autowired
    private MessageSourceOnlyLanguageService messageSourceOnlyLanguageService;

    @GetMapping()
    public Map<Object, Object> getFrontend(
            @RequestParam String lang
    ) {
        Locale locale = lang == null ? new Locale(langConfig.getDefaultLang().getName()) : new Locale(lang);
        HashMap<Object, Object> frontend = new HashMap<>();
        frontend.put("config", frontendConfig.getConfig());
        frontend.put("version", frontendConfig.getVersion());
        frontend.put("langLangs", langConfig.getLangs());
        frontend.put("langMap", messageSourceOnlyLanguageService.getMessages(locale));
        return frontend;
    }
}
