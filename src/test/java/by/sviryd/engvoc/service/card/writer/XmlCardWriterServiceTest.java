package by.sviryd.engvoc.service.card.writer;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.service.card.reader.XmlCardReaderService;
import by.sviryd.engvoc.type.LangLocale;
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
public class XmlCardWriterServiceTest {
    private final File source = new File("./src/main/resources/test/xml/dictionary/ClothesEnRu.xml");
    private final File dest = new File("./src/main/resources/test/xml/dictionary/emptyEnRu.xml");
    @Autowired
    private XmlCardWriterService service;
    @Autowired
    private XmlCardReaderService readerService;

    @Test
    public void write() throws Exception {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setSource(LangLocale.en_US);
        vocabulary.setTarget(LangLocale.ru_RU);
        Dictionary dictionary = new Dictionary();
        dictionary.setVocabulary(vocabulary);
        List<Card> cards = readerService.extract(source);
        int sizeRead = cards.size();
        service.write(dest, cards, dictionary);
        cards = readerService.extract(dest);
        int sizeWrite = cards.size();
        Assert.assertEquals(sizeRead, sizeWrite);
    }
}