package by.sviryd.engvoc.service.namePathReplacementEntity;

import by.sviryd.engvoc.config.ServerLanguageConfig;
import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.service.PropertyService;
import by.sviryd.engvoc.service.namePathReplacement.NamePathReplacementTranslatorService;
import by.sviryd.engvoc.service.namePathReplacement.PathReplacementCommonService;
import by.sviryd.engvoc.util.PagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyNamePathReplacementService {
    private static final int BATCH_SIZE = 100;
    @Autowired
    private NamePathReplacementTranslatorService namePathReplacementTranslatorService;
//    @Autowired
//    private NamePathReplacementCyrillicLatinTranslitService namePathReplacementCyrillicLatinTranslitService;
    @Autowired
    private PathReplacementCommonService pathReplacementCommonService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private ServerLanguageConfig serverLanguageConfig;

    public void adjustPaths(String langFrom) throws Exception {
        long count = propertyService.count();
        int countPages = PagerUtil.getCountPages(BATCH_SIZE, count);
        for (int i = 0; i < countPages; i++) {
            Pageable pageable = PageRequest.of(i, BATCH_SIZE);
            Page<Property> all = propertyService.findAll(pageable);
            List<Property> properties = all.getContent();
            adjustPaths(properties, langFrom);
            propertyService.saveAll(properties);
        }
    }

    public void adjustPaths(List<Property> properties, String langFrom) throws Exception {
        namePathReplacementTranslatorService.adjustPaths(properties, langFrom, serverLanguageConfig.getLanguage());
//        namePathReplacementCyrillicLatinTranslitService.adjustPaths(properties);
        pathReplacementCommonService.adjustPath(properties);
    }
}
