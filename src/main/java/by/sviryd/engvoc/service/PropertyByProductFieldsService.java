package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.PropertyByProductFieldsConfig;
import by.sviryd.engvoc.domain.Property;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class PropertyByProductFieldsService {
    @Autowired
    private PropertyByProductFieldsConfig config;
    @Getter
    private List<Property> propertyByProductFields;

    @Autowired
    private PropertyService propertyService;

    @PostConstruct
    public void init() {
        propertyByProductFields = propertyService.saveAll(config.getPropertiesByProductFields(), null);
    }
}
