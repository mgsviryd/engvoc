package by.sviryd.engvoc.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MultipartFileUtil {
    public static MultipartFile getMultipartFile(File file) throws IOException {
        return new MockMultipartFile(file.getName(), Files.newInputStream(file.toPath()));
    }
}