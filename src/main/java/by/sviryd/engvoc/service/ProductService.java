package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.domain.PropertyData;
import by.sviryd.engvoc.domain.dto.PropertyOptionsDTO;
import by.sviryd.engvoc.repos.ProductRepo;
import by.sviryd.engvoc.service.productPathExtractor.IProductPathExtractor;
import by.sviryd.engvoc.service.stringReplacementService.URLEncodingCustomReplacementService;
import by.sviryd.engvoc.type.PropertyType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductFieldsByPropertyService productFieldsByPropertyService;
    @Autowired
    private PropertyByProductFieldsService propertyByProductFieldsService;
    @Autowired
    private URLEncodingCustomReplacementService urlEncodingCustomReplacementService;


    public long count() {
        return productRepo.count();
    }

    public void delete(Product product) {
        productRepo.delete(product);
    }

    public List<Product> findAll() {
        return productRepo.findAll();
    }

    public List<Product> findAllById(List<Long> ids) {
        return productRepo.findAllById(ids);
    }

    public Product save(Product product) {
        return productRepo.save(product);
    }

    public List<Product> saveAll(List<Product> products) {
        return productRepo.saveAll(products);
    }

    public boolean existsByCode(Product product) {
        return productRepo.existsByCode(product.getCode());
    }

    public Page<Product> getProducts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return productRepo.findAll(pageable);
    }

    public void updatePaths(IProductPathExtractor extractor, int sizeBunch) throws Exception {
        long count = productRepo.count();
        long remaider = count % sizeBunch;
        int countPages;
        if (remaider > 0) {
            countPages = (int) (count / sizeBunch) + 1;
        } else {
            countPages = (int) (count / sizeBunch);
        }
        for (int i = 0; i < countPages; i++) {
            Page<Product> products = getProducts(i, sizeBunch);
            List<Product> content = products.getContent();
            for (Product product : content) {
                String path = extractor.getPath(product);
                product.setPath(path);
            }
            saveAll(content);
        }
    }

    public List<String> getUrls() {
        return productRepo.getUrls();
    }

    public List<URL> getUrlsStartWith(String text) {
        return productRepo.getUrlsStartWith(text);
    }

    public Page<Product> getProductsByCategory(Category category, Pageable pageable) {
        return productRepo.getProductsByCategory(category, pageable);
    }

    public boolean existsByCode(String code) {
        return productRepo.existsByCode(code);
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    public List<PropertyOptionsDTO> getProductFieldOptionsDTO(Category category) {
        List<String> fields = productFieldsByPropertyService.getFields();
        List<Product> productsByCategory = productRepo.getProductsByCategory(category);
        List<Property> properties = propertyByProductFieldsService.getPropertyByProductFields();
        return transformToPropertyOptionsDTOs(productsByCategory, properties, fields);
    }

    private List<PropertyOptionsDTO> transformToPropertyOptionsDTOs(List<Product> products, List<Property> properties, List<String> fields) {
        List<PropertyOptionsDTO> options = new ArrayList<>();
        for (int i = 0; i < properties.size(); i++) {
            String field = fields.get(i);
            Property property = properties.get(i);
            PropertyOptionsDTO option = new PropertyOptionsDTO(property);
            for (Product p : products) {
//                Field f = ReflectionUtils.findField(Product.class, field);
//                Object data = ReflectionUtils.getField(f, p);
                Object data = null;
                try {
                    data = PropertyUtils.getProperty(p, field);
                } catch (Exception e) {
                    log.error(e.getMessage(), p, field);
                }
                if (data != null) {
                    option = addDataToOption(option, data);
                }
            }
            if (!option.isEmpty()) {
                options = PropertyOptionsDTO.adjustMinMax(options);
                options.add(option);
            }
        }
        return options;
    }

    private PropertyOptionsDTO addDataToOption(PropertyOptionsDTO dto, Object data) {
        Property property = dto.getProperty();
        PropertyType type = property.getType();
        String path = urlEncodingCustomReplacementService.replace(data.toString());
        PropertyData propertyData = new PropertyData(path);
        switch (type) {
            case STRING:
                propertyData.setName((String) data);
                break;
            case DOUBLE:
                propertyData.setDoubleData((Double) data);
                break;
            case INTEGER:
                propertyData.setIntegerData((Integer) data);
                break;
            case BOOLEAN:
                propertyData.setBooleanData((Boolean) data);
                break;
        }
        dto.addChoice(propertyData);
        return dto;
    }

}
