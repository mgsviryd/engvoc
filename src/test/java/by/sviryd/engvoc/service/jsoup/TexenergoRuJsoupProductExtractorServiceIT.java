package by.sviryd.engvoc.service.jsoup;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.service.jsoup.extractor.TexenergoRuJsoupProductURLExtractor;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TexenergoRuJsoupProductExtractorServiceIT {
    @Autowired
    private TexenergoRuJsoupProductURLExtractor service;
    @Autowired
    private JsoupDocService jsoupDocService;
    private String url = "https://www.texenergo.ru/catalog/list.html/433310";
    private Document doc;

    @Before
    public void init() throws IOException {
        doc = jsoupDocService.getJsoupDoc(url);
    }

    @Test
    public void getCategories() throws Exception {
        List<Category> categories = service.getCategories(doc);
    }

    @Test
    public void getProperties() throws Exception {
        List<Property> properties = service.getProperties(doc);
    }

    @Test
    public void getPropertiesValue() throws Exception {
        List<String> values = service.getPropertyValues(doc);
    }

    @Test
    public void getImagesUrl() throws Exception {
        List<URL> urls = service.getImagesUrl(doc);
    }

    @Test
    public void getDescriptionImageUrl() throws Exception {
        List<URL> urls = service.getDescriptionImagesUrl(doc);
    }

}