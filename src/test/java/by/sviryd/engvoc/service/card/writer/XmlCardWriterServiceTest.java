package by.sviryd.engvoc.service.card.writer;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.service.card.reader.XmlCardReaderService;
import by.sviryd.engvoc.util.MultipartFileUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XmlCardWriterServiceTest {
    @Autowired
    private XmlCardWriterService service;
    @Autowired
    private XmlCardReaderService readerService;

    private final File source = new File("./src/main/resources/test/xml/dictionary/ClothesEnRu.xml");
    private final File dest = new File("./src/main/resources/test/xml/dictionary/emptyEnRu.xml");

    @Test
    public void write() throws Exception {
        List<Card> cards = readerService.extract(source);
        int sizeRead = cards.size();
        service.write(dest, cards);
        cards = readerService.extract(dest);
        int sizeWrite = cards.size();
        Assert.assertEquals(sizeRead, sizeWrite);
    }
}