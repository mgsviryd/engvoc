package by.sviryd.engvoc.io;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
@Slf4j
public class MultipartDownloader {
    public boolean download(MultipartFile multipartFile, String pathName) {
        try {
            multipartFile.transferTo(new File(pathName));
            return true;
        } catch (Exception e) {
            log.error("Cannot copy multipart: " + multipartFile.getName() + "to file: " + pathName, e);
            return false;
        }
    }
}
