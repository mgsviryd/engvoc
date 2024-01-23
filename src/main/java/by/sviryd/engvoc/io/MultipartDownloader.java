package by.sviryd.engvoc.io;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class MultipartDownloader {
    public boolean download(MultipartFile multipartFile, String pathName) {
        try {
            multipartFile.transferTo(new File(pathName));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
