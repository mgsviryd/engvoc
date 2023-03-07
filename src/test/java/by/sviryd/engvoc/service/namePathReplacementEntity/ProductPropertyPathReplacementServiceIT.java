package by.sviryd.engvoc.service.namePathReplacementEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductPropertyPathReplacementServiceIT {
    @Autowired
    private ProductPropertyPathReplacementService service;
    @Test
    public void adjustPaths() throws Exception {
        service.adjustPaths();
    }

}