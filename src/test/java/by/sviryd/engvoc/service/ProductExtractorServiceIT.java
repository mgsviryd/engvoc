package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.ExtractorError;
import by.sviryd.engvoc.excel.ExcelCellReader;
import by.sviryd.engvoc.service.jsoup.extractor.S3RuJsoupProductURLExtractor;
import by.sviryd.engvoc.service.jsoup.extractor.TexenergoRuJsoupProductURLExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductExtractorServiceIT {
    @Autowired
    private ProductURLExtractorService service;
    @Autowired
    private ExcelCellReader excelCellReader;
    @Autowired
    private S3RuJsoupProductURLExtractor s3RuExtractor;
    @Autowired
    private TexenergoRuJsoupProductURLExtractor texenergoRuExtractor;


    @Test
    public void saveProducts() throws Exception {
        int iRowStart = 0;
        int iRowEnd = 12152;
        int iColumn = 4;
        String path = "d:\\java\\projects\\attrade-doc\\s3ru.xls";
        Stream<URL> urls = excelCellReader.getURLs(iRowStart, iRowEnd, iColumn, path);
        ExtractorError extractorError = service.saveProductsIfNotExistsByCodeAndSaveErrors(s3RuExtractor, urls);
        extractorError.getUrls().forEach(System.out::println);
    }

    @Test
    public void saveS3Product() throws Exception {
        URL url = new URL("https://www.s3.ru/ru/item/46865");
        service.saveProductIfNotExistsByCodeAndSaveError(s3RuExtractor, url);
    }

    @Test
    public void saveTexenergoProduct() throws Exception {
        URL url = new URL("https://www.texenergo.ru/catalog/item.html/te00433560");
        service.saveProductIfNotExistsByCodeAndSaveError(texenergoRuExtractor, url);
    }

    @Test
    public void saveTexenergoDomainProducts() throws Exception {
        service.saveProductsIfNotExistsByCodeAndSaveErrors(texenergoRuExtractor);
    }

    @Test
    public void saveS3DomainProducts() throws Exception {
        service.saveProductsIfNotExistsByCodeAndSaveErrors(s3RuExtractor);
    }
}