package by.sviryd.engvoc.service.namePathReplacementEntity;

import by.sviryd.engvoc.config.ServerLanguageConfig;
import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.service.ProductService;
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
public class ProductNamePathReplacementService {
    private static final int BATCH_SIZE = 100;
    @Autowired
    private NamePathReplacementTranslatorService namePathReplacementTranslatorService;
    @Autowired
    private PathReplacementCommonService pathReplacementCommonService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ServerLanguageConfig serverLanguageConfig;

    public void adjustPaths(String langFrom) throws Exception {
        long count = productService.count();
        int countPages = PagerUtil.getCountPages(BATCH_SIZE, count);
        for (int i = 0; i < countPages; i++) {
            Pageable pageable = PageRequest.of(i, BATCH_SIZE);
            Page<Product> all = productService.findAll(pageable);
            List<Product> properties = all.getContent();
            adjustPaths(properties, langFrom);
            productService.saveAll(properties);
        }
    }

    public void adjustPaths(List<Product> properties, String langFrom) throws Exception {
        namePathReplacementTranslatorService.adjustPaths(properties, langFrom, serverLanguageConfig.getLanguage());
        pathReplacementCommonService.adjustPath(properties);
    }
}
