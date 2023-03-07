package by.sviryd.engvoc.service.namePathReplacementEntity;

import by.sviryd.engvoc.service.jsoup.extractor.TexenergoRuJsoupProductURLExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertyNamePathReplacementServiceIT {
    @Autowired
    private PropertyNamePathReplacementService service;
    @Autowired
    private TexenergoRuJsoupProductURLExtractor extractor;
    @Test
    public void adjustPaths() throws Exception {
        service.adjustPaths(extractor.getLocale().getLanguage());
    }

}