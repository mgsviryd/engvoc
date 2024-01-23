package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.ServerPathConfig;
import by.sviryd.engvoc.io.MultipartDownloader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AudioMediaService {
    @Autowired
    private ServerPathConfig serverPathConfig;

    @Autowired
    private MultipartDownloader multipartDownloader;

    @PostConstruct
    public void init() throws IOException {
    }

    public String saveOrRollback(MultipartFile file) {
        if (file == null) {
            return null;
        }
        String targetPathName = getMediaPathNameAsUUID(file.getOriginalFilename());
        if (save(file, targetPathName)) {
            Path fileName = Paths.get(targetPathName).getFileName();
            return fileName.toString();
        } else {
            return null;
        }
    }

    private boolean save(MultipartFile file, String pathName) {
        return multipartDownloader.download(file, pathName);
    }

    private String getMediaPathNameAsUUID(String pathName) {
        return serverPathConfig.getAbsolute() + serverPathConfig.getUploadAudio() + File.separator + getPictureFileNameAsUUID(pathName);
    }

    public String getPictureFileNameAsUUID(String url) {
        String extension = FilenameUtils.getExtension(url).toLowerCase();
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }
}
