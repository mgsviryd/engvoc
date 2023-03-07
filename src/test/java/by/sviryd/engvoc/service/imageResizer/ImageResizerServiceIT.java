package by.sviryd.engvoc.service.imageResizer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageResizerServiceIT {
    @Autowired
    private ImageResizerService imageResizerService;
    String folder = "d:\\image\\compress";

    @Test
    public void compressAll() throws IOException {
        Files.walk(Paths.get(folder)).filter(Files::isRegularFile).forEach(path -> {
            try {
                imageResizerService.compressImage(path.toString(), 100);
            } catch (Exception e) {
                System.out.println("EXCEPTION");
            }
        });
    }
}