package by.sviryd.engvoc.service.stringReplacementService;

import by.sviryd.engvoc.config.PropertyByProductFieldsConfig;
import by.sviryd.engvoc.domain.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrlReservedWordsProductFieldsStringReplacementService implements IStringReplacementService {
    private static final String THE = "the-";
    @Autowired
    private PropertyByProductFieldsConfig config;
    private List<String> propertyNames;

    @PostConstruct
    public void init() {
        propertyNames = config.getPropertiesByProductFields().stream().map(Property::getPath).collect(Collectors.toList());
    }

    @Override
    public String replace(String word) {
        if (word != null) {
            if (propertyNames.contains(word.toLowerCase())) {
                return THE + word;
            } else {
                return word;
            }
        } else {
            return null;
        }
    }
}
