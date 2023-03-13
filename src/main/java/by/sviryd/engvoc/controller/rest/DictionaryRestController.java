package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.DictionaryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("json/dictionary")
public class DictionaryRestController {
    @Autowired
    private DictionaryConfig dictionaryConfig;

    @GetMapping("supported")
    public List<String> supported() {
        return dictionaryConfig.getSupported();
    }

}
