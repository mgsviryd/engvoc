package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.service.stringReplacementService.URLKeysEntityFieldsStringReplacementService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductFieldsByPropertyService {
    @Autowired
    private URLKeysEntityFieldsStringReplacementService stringReplacementService;

    @Autowired
    private PropertyByProductFieldsService propertyByProductFieldsService;
    @Getter
    private List<String> fields;

    @PostConstruct
    public void init() {
        fields = new ArrayList<>();
        for (Property property : propertyByProductFieldsService.getPropertyByProductFields()) {
            fields.add(stringReplacementService.replace(property.getPath()));
        }
    }
}
