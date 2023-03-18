package by.sviryd.engvoc.service.card.reader;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.util.MultipartFileUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExcelCardShortReaderServiceTest {
    private final String sheetName = "f";
    private final int countInBook = 8530;
    private final int countInSheet = 280;
    private final File file = new File("./src/main/resources/test/excel/vocabularyEnRu.xlsx");
    @Autowired
    private ExcelCardShortReaderService service;

    @Before
    public void init() {
    }

    @Test
    public void testExtract() {
        List<Card> cards = service.extract(file);
        Assert.assertEquals(countInBook, cards.size());
    }

    @Test
    public void testExtract1() {
        List<Card> cards = service.extract(file, sheetName);
        Assert.assertEquals(countInSheet, cards.size());
    }

    @Test
    public void testExtract2() {
        int count = 0;
        try (FileInputStream fip = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fip)) {
            List<Card> cards = service.extract(workbook);
            count = cards.size();
        } catch (Exception e) {
        }
        Assert.assertEquals(countInBook, count);
    }

    @Test
    public void testExtract3() {
        int count = 0;
        try (FileInputStream fip = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fip)) {
            List<Card> cards = service.extract(workbook.getSheet(sheetName));
            count = cards.size();
        } catch (Exception e) {
        }
        Assert.assertEquals(countInSheet, count);
    }
    public void testExtract4() throws IOException {
        MultipartFile multipartFile = MultipartFileUtil.getMultipartFile(file);
        List<Card> cards = service.extract(multipartFile);
        int count = cards.size();
        Assert.assertEquals(countInBook, count);
    }
    public void testExtract5() throws IOException {
        MultipartFile multipartFile = MultipartFileUtil.getMultipartFile(file);
        List<Card> cards = service.extract(multipartFile, sheetName);
        int count = cards.size();
        Assert.assertEquals(countInSheet, count);
    }
}