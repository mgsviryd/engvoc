package by.sviryd.engvoc.service.card.reader;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;
@RunWith(SpringRunner.class)
@SpringBootTest
public class XmlCardReaderServiceTest {
    @Autowired
    private XmlCardReaderService service;
    private final File file = new File("./src/main/resources/test/xml/dictionary/ClothesEnRu.xml");
    private final File file1 = new File("./src/main/resources/test/xml/dictionary/FeelingEnRu.xml");
    private final File file2 = new File("./src/main/resources/test/xml/dictionary/HealthEnRu.xml");
    private final Card card = Card
            .builder()
            .word("pleat")
            .translation("складка")
            .example("wide/narrow pleats")
            .exampleTranslation("широкие/узкие складки")
            .transcription("pliːt")
            .learned(true)
            .dictionary(new Dictionary("Clothes"))
            .build();
    private final int sizeFile = 125;
    private final int sizeFiles = 519;

    @Before
    public void init() {
    }

    @Test
    public void extract() throws Exception {
        List<Card> cards = service.extract(file);
        Assert.assertTrue(cards.contains(card));
        Assert.assertTrue(cards.size() == sizeFile);
    }

    @Test
    public void testExtract() throws Exception {
        List<Card> cards = service.extract(Arrays.asList(file,file1,file2));
        Assert.assertTrue(cards.contains(card));
        Assert.assertTrue(cards.size() == sizeFiles);
    }
}