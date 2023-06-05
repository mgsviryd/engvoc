package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.FrontendConfig;
import by.sviryd.engvoc.config.LocaleConfig;
import by.sviryd.engvoc.service.MessageI18nService;
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
    private LocaleConfig langConfig;
    @Autowired
    private MessageI18nService messageI18nService;

    @GetMapping()
    public Map<Object, Object> getFrontend(
            @RequestParam String lang,
            Locale locale
    ) {
        HashMap<Object, Object> frontend = new HashMap<>();
        frontend.put("config", frontendConfig.getConfig());
        frontend.put("version", frontendConfig.getVersion());
        frontend.put("langLangs", langConfig.getLangLocales());
        frontend.put("langMap", messageI18nService.getMessages(locale));
        return frontend;
    }
}
