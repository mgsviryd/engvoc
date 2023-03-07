package by.sviryd.engvoc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PictureMediaServiceIT {
    @Autowired
    private PictureMediaService pictureMediaService;

    @Test
    public void saveDefaultPicture() throws Exception {
        URL defaultImageUrl = new URL("http://www.s3.ru/thumb/catalog_big/images/no-photo.png");
        pictureMediaService.saveDefaultPictureOrRollback(defaultImageUrl);
    }

}