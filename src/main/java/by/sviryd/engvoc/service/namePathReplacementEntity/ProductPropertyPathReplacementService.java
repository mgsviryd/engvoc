package by.sviryd.engvoc.service.namePathReplacementEntity;

import by.sviryd.engvoc.domain.ProductProperty;
import by.sviryd.engvoc.service.ProductPropertyService;
import by.sviryd.engvoc.service.namePathReplacement.NamePathReplacementCyrillicLatinTranslitService;
import by.sviryd.engvoc.service.namePathReplacement.PathReplacementCommonService;
import by.sviryd.engvoc.util.PagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductPropertyPathReplacementService {
    private static final int BATCH_SIZE = 10000;
    @Autowired
    private NamePathReplacementCyrillicLatinTranslitService namePathReplacementCyrillicLatinTranslitService;
    @Autowired
    private PathReplacementCommonService pathReplacementCommonService;
    @Autowired
    private ProductPropertyService productPropertyService;

    public void adjustPaths() {
        long count = productPropertyService.count();
        int countPages = PagerUtil.getCountPages(BATCH_SIZE, count);
        for (int i = 0; i < countPages; i++) {
            Pageable pageable = PageRequest.of(i, BATCH_SIZE);
            Page<ProductProperty> all = productPropertyService.findAll(pageable);
            List<ProductProperty> properties = all.getContent();
            adjustPaths(properties);
            productPropertyService.saveAll(properties);
        }
    }

    public void adjustPaths(List<ProductProperty> properties) {
        namePathReplacementCyrillicLatinTranslitService.adjustPaths(properties);
        pathReplacementCommonService.adjustPath(properties);
    }
}
