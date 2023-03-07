package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.PictureMediaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("pictureMedia")
public class PictureMediaRestController {
    @Autowired
    private PictureMediaConfig pictureMediaConfig;

    @GetMapping
    public Map<Object, Object> list() {
        Map<Object, Object> frontend = new HashMap<>();
        frontend.put("pictureMedias", pictureMediaConfig.getPictureMedias());
        frontend.put("defaultPictureFileName", pictureMediaConfig.getDefaultPictureFileName());
        return frontend;
    }
}
