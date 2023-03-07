package by.sviryd.engvoc.service.productPathExtractor;

import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.service.translit.BackSlashHyphenTranslitService;
import by.sviryd.engvoc.service.translit.CyrillicLatinTranslitService;
import by.sviryd.engvoc.service.translit.ForwardSlashHyphenTranslitService;
import by.sviryd.engvoc.service.translit.SpaceHyphenTranslitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PathProductByNameExtractorService implements IProductPathExtractor {
    @Autowired
    private CyrillicLatinTranslitService cyrillicLatinTranslitService;
    @Autowired
    private SpaceHyphenTranslitService spaceHyphenTranslitService;
    @Autowired
    private ForwardSlashHyphenTranslitService forwardSlashHyphenTranslitService;
    @Autowired
    private BackSlashHyphenTranslitService backSlashHyphenTranslitService;


    @Override
    public String getPath(Product product) {
        String s = cyrillicLatinTranslitService.toTranslit(product.getName());
        s = spaceHyphenTranslitService.toTranslit(s);
        s = forwardSlashHyphenTranslitService.toTranslit(s);
        s = backSlashHyphenTranslitService.toTranslit(s);
        return s.toLowerCase();
    }
}
