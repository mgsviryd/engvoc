package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.*;
import by.sviryd.engvoc.domain.dto.PropertyOptionsDTO;
import by.sviryd.engvoc.repos.ProductPropertyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductPropertyService {
    @Autowired
    private ProductPropertyRepo productPropertyRepo;

    public long count() {
        return productPropertyRepo.count();
    }

    public Page<ProductProperty> findAll(Pageable pageable) {
        return productPropertyRepo.findAll(pageable);
    }

    public ProductProperty save(ProductProperty productProperty) {
        return productPropertyRepo.save(productProperty);
    }

    public List<ProductProperty> saveAll(List<ProductProperty> properties) {
        return productPropertyRepo.saveAll(properties);
    }

    public List<PropertyOptionsDTO> getProductPropertyOptionsDTO(Category category) {
        List<Property> properties = category.getProperties();
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object[]> data = productPropertyRepo.getProductPropertyData(properties);
        return transformToPropertyChoicesDTOs(data);
    }

    private List<PropertyOptionsDTO> transformToPropertyChoicesDTOs(List<Object[]> data) {
        List<PropertyOptionsDTO> choicesDTOS = new ArrayList<>();
        for (Object[] o : data) {
            Property property = (Property) o[0];
            PropertyData propertyData = (PropertyData) o[1];
            Optional<PropertyOptionsDTO> founded = getPropertyChoicesDTOByProperty(property, choicesDTOS);
            if (founded.isPresent()) {
                founded.get().addChoice(propertyData);
            } else {
                PropertyOptionsDTO dto = new PropertyOptionsDTO(property);
                dto.addChoice(propertyData);
                choicesDTOS.add(dto);
            }
        }
        choicesDTOS = PropertyOptionsDTO.adjustMinMax(choicesDTOS);
        return choicesDTOS;
    }

    private Optional<PropertyOptionsDTO> getPropertyChoicesDTOByProperty(Property property, List<PropertyOptionsDTO> choicesDTOS) {
        return choicesDTOS.stream().filter(x -> x.getProperty().equals(property)).findFirst();
    }

    public Page<Product> getProducts(List<PropertyOptionsDTO> activeProductPropertyOptions, List<PropertyOptionsDTO> activeProductFieldsOptions, Category category, Pageable pageable) {
        return productPropertyRepo.getProducts(activeProductPropertyOptions, activeProductFieldsOptions, category, pageable);
    }
}
