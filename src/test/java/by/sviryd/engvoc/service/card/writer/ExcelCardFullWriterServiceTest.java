package by.sviryd.engvoc.service.card.writer;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.service.card.reader.ExcelCardFullReaderService;
import by.sviryd.engvoc.service.card.reader.ExcelCardShortReaderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExcelCardFullWriterServiceTest {
    private final File source = new File("./src/main/resources/test/excel/vocabularyEnRu.xlsx");
    private final File dest = new File("./src/main/resources/test/excel/emptyEnRu.xlsx");
    @Autowired
    private ExcelCardFullWriterService writerService;
    @Autowired
    private ExcelCardShortReaderService readerService;

    @Test
    public void write() throws Exception {
        List<Card> cards = readerService.extract(source);
        Card card = cards.get(0);
        int size = cards.size();
        writerService.write(cards, dest, "test");
        cards = readerService.extract(dest);
        Assert.assertEquals(card,(cards.get(0)));
        Assert.assertEquals(size, cards.size());
    }
}