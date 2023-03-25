package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.PictureMediaConfig;
import by.sviryd.engvoc.service.PictureMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("json/pictureMedia")
public class PictureMediaRestController {
    @Autowired
    private PictureMediaConfig pictureMediaConfig;
    @Autowired
    private PictureMediaService pictureMediaService;

    @GetMapping
    public Map<Object, Object> list() {
        Map<Object, Object> frontend = new HashMap<>();
        frontend.put("pictureMedias", pictureMediaConfig.getPictureMedias());
        frontend.put("defaultPictureFileName", pictureMediaConfig.getDefaultPictureFileName());
        return frontend;
    }

    @PostMapping("savePicture")
    public String savePicture(
            @RequestParam("file") MultipartFile file
    ) {
        return pictureMediaService.savePictureOrRollback(file);
    }
}
