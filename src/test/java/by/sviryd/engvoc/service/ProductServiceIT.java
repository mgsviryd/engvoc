package by.sviryd.engvoc.service;

import by.sviryd.engvoc.service.productPathExtractor.PathProductByNameExtractorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceIT {
    @Autowired
    private ProductService productService;
    @Autowired
    private PathProductByNameExtractorService extractor;
    @Test
    public void updatePaths() throws Exception {
        productService.updatePaths(extractor, 100);
    }

}