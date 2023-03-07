package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.PropertyByProductFieldsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class URLReservedWordsProductFieldsService {
    @Autowired
    private PropertyByProductFieldsConfig propertyByProductFieldsConfig;

    public boolean isReserved(String word) {
        return propertyByProductFieldsConfig.getPaths().contains(word);
    }
}
