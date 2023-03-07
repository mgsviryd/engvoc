package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.repos.PropertyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    @Autowired
    private PropertyRepo propertyRepo;

    public long count(){return propertyRepo.count();}
    public Page<Property> findAll(Pageable pageable){return propertyRepo.findAll(pageable);}

    public Property save(Property property) {
        return propertyRepo.save(property);
    }

    public List<Property> findByCategoryAndName(Property property) {
        return propertyRepo.findByCategoryAndName(property.getCategory(), property.getName());
    }

    public List<Property> saveAll(List<Property> properties) {
        return propertyRepo.saveAll(properties);
    }

    public List<Property> saveAll(List<Property> properties, Category category) {
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            p.setCategory(category);
            Property save;
            List<Property> list = findByCategoryAndName(p);
            if (list.isEmpty()) {
                save = save(p);
            } else {
                save = list.get(0);
            }
            properties.set(i, save);
        }
        return properties;
    }
}
