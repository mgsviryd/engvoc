package by.sviryd.engvoc.service.card;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DictionaryBindServiceTest {
    @Autowired
    private DictionaryBindService service;

    @Test
    public void isSupported() {
        Assert.assertFalse(service.isSupportedAbbr("dictionary"));
        Assert.assertFalse(service.isSupportedAbbr("dictionary.xml"));
        Assert.assertFalse(service.isSupportedAbbr("dictionaryFrRu.xml"));
        Assert.assertTrue(service.isSupportedAbbr("ru_RUen_US"));
    }

    @Test
    public void getSourceAbbr() {
        Assert.assertNull(service.getSourceAbbr("dictionary"));
        Assert.assertNull(service.getSourceAbbr("dictionary.xml"));
        Assert.assertNull(service.getSourceAbbr("dictionaryEn.xml"));
        Assert.assertEquals(service.getSourceAbbr("en_USru_RU"), "en_US");
    }

    @Test
    public void getDestinationAbbr() {
        Assert.assertNull(service.getDestinationAbbr("dictionary"));
        Assert.assertNull(service.getDestinationAbbr("dictionary.xml"));
        Assert.assertNull(service.getDestinationAbbr("dictionaryEn.xml"));
        Assert.assertEquals(service.getDestinationAbbr("en_USru_RU"), "ru_RU");
    }

    @Test
    public void getSourceAbbrId() {
        Assert.assertNull(service.getSourceAbbrId("dictionary"));
        Assert.assertNull(service.getSourceAbbrId("dictionary.xml"));
        Assert.assertNull(service.getSourceAbbrId("dictionaryEn.xml"));
        Assert.assertEquals(service.getSourceAbbrId("en_USru_RU"), new Integer(1033));
    }

    @Test
    public void getDestinationAbbrId() {
        Assert.assertNull(service.getDestinationAbbrId("dictionary"));
        Assert.assertNull(service.getDestinationAbbrId("dictionary.xml"));
        Assert.assertNull(service.getDestinationAbbrId("dictionaryEn.xml"));
        Assert.assertEquals(service.getDestinationAbbrId("en_USru_RU"), new Integer(1049));
    }

    @Test
    public void isDictionary() {
        Assert.assertFalse(service.isDictionary("dictionary"));
        Assert.assertFalse(service.isDictionary("dictionary.xml"));
        Assert.assertFalse(service.isDictionary("dictionaryEn.xml"));
        Assert.assertTrue(service.isDictionary("dictionary_en_USru_RU.xml"));
    }

    @Test
    public void getDictionaryNameWithoutAbbr() {
        Assert.assertNull(service.getDictionaryNameWithoutAbbr("dictionary"));
        Assert.assertNull(service.getDictionaryNameWithoutAbbr("dictionary.xml"));
        Assert.assertNull(service.getDictionaryNameWithoutAbbr("dictionaryEn.xml"));
        Assert.assertEquals(service.getDictionaryNameWithoutAbbr("dictionary_en_USru_RU.xml"), "dictionary_");
    }

    @Test
    public void getDictionaryAbbr() {
        Assert.assertNull(service.getDictionaryAbbr("dictionary"));
        Assert.assertNull(service.getDictionaryAbbr("dictionary.xml"));
        Assert.assertNull(service.getDictionaryAbbr("dictionaryEn.xml"));
        Assert.assertEquals(service.getDictionaryAbbr("dictionary_en_USru_RU.xml"), "en_USru_RU");
    }
}