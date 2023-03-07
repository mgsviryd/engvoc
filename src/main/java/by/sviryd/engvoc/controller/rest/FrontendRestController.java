package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.FrontendConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/json/frontend")
public class FrontendRestController {
    @Autowired
    private FrontendConfig frontendConfig;

    @GetMapping()
    public Map<Object, Object> getFrontend() {
        HashMap<Object, Object> frontend = new HashMap<>();
        frontend.put("config", frontendConfig.getConfig());
        frontend.put("version", frontendConfig.getVersion());
        return frontend;
    }

//    @GetMapping("config")
//    public Map<String, String> getFrontendConfig() {
//        return frontendConfig.getConfig();
//    }
//
//    @GetMapping("version")
//    public Map<String, String> getFrontendVersion() {
//        return frontendConfig.getVersion();
//    }
}
