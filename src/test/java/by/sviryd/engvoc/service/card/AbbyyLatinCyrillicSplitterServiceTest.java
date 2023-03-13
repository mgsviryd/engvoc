package by.sviryd.engvoc.service.card;

import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbbyyLatinCyrillicSplitterServiceTest {
//    private AbbyyLatinCyrillicSplitterConfig config;
    @Autowired
    private AbbyyLatinCyrillicSplitterService service;
    private final String latin = "word";
    private final String cyrillic = "слово";

    @Before
    public void init() {
//        config = new Config().getAbbyyLatinCyrillicSplitterConfig();
//        service = new AbbyyLatinCyrillicSplitterService(config);

    }

    @Test
    public void getPair() {
        Pair<String, String> pair = service.getPair(latin + " " + "—" + " " + cyrillic);
        Pair<String, String> pair1 = service.getPair(latin + " " + "-" + " " + cyrillic);
        Pair<String, String> pair2 = service.getPair(latin + " " + "--" + " " + cyrillic);
        Pair<String, String> pair3 = service.getPair(latin + " " + "—" + cyrillic);
        Pair<String, String> pair4 = service.getPair(latin + "-" + " " + cyrillic);
        Pair<String, String> pair5 = service.getPair(latin + "--" + cyrillic);

        Assert.assertTrue(latin.equals(pair.getKey()) && cyrillic.equals(pair.getValue()));
        Assert.assertTrue(latin.equals(pair1.getKey()) && cyrillic.equals(pair1.getValue()));
        Assert.assertTrue(latin.equals(pair2.getKey()) && cyrillic.equals(pair2.getValue()));
        Assert.assertTrue(latin.equals(pair3.getKey()) && cyrillic.equals(pair3.getValue()));
        Assert.assertTrue(latin.equals(pair4.getKey()) && cyrillic.equals(pair4.getValue()));
        Assert.assertTrue(latin.equals(pair5.getKey()) && cyrillic.equals(pair5.getValue()));
    }

    @Test
    public void isCyrillic() {
        Assert.assertTrue(service.isCyrillic(cyrillic.charAt(0)));
        Assert.assertFalse(service.isCyrillic(latin.charAt(0)));
    }
}