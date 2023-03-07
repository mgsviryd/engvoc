package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.*;
import by.sviryd.engvoc.domain.dto.PageDTO;
import by.sviryd.engvoc.domain.dto.PropertyOptionsDTO;
import by.sviryd.engvoc.gson.GsonExcludeStrategies;
import by.sviryd.engvoc.service.*;
import by.sviryd.engvoc.service.stringReplacementService.UrlEncodingASCIIStringReplacementService;
import by.sviryd.engvoc.type.PropertyType;
import by.sviryd.engvoc.util.ObjectToViewTransformUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/json/category")
public class CategoryRestController {
    private static final String MIN_MAX_MARK = "min-max";

    @Autowired
    private URLReservedWordsCommonService urlReservedWordsCommonService;

    @Autowired
    private URLReservedWordsProductFieldsService urlReservedWordsProductFieldsService;

    @Autowired
    private PropertyByProductFieldsService propertyByProductFieldsService;

    @Autowired
    private CategoryHierarchyService categoryCategoryHierarchyService;

    @Autowired
    private ProductPropertyService productPropertyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UrlEncodingASCIIStringReplacementService urlEncodingASCIIStringReplacementService;

    @Autowired
    private GsonExcludeStrategies gsonExcludeStrategies;

    @GetMapping(value = "/{path}/{id}")
    public String categoryPage(
            @PathVariable String path,
            @PathVariable Long id,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "popular") String property,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "false", name = "is-need-property-choices") boolean isNeedPropertyChoices,
            @RequestParam MultiValueMap<String, String> queryMap
    ) {
        Sort.Direction directionSort = Sort.Direction.fromString(direction);
        Sort.Order order = new Sort.Order(directionSort, property);
        Pageable pageable = PageRequest.of(page, size, new Sort(Collections.singletonList(order)));
        Category category = categoryCategoryHierarchyService.getById(id);
        List<PropertyOptionsDTO> activeProductPropertyOptions = getActivePropertyOptions(category, queryMap);
        List<PropertyOptionsDTO> activeProductFieldsOptions = getActiveProductFieldsOptions(queryMap);
        Page<Product> pageProducts;
        if (isOptionsAbsent(activeProductPropertyOptions, activeProductFieldsOptions)) {
            pageProducts = productService.getProductsByCategory(category, pageable);
        } else {
            pageProducts = productPropertyService.getProducts(
                    activeProductPropertyOptions,
                    activeProductFieldsOptions,
                    category,
                    pageable
            );
        }
        PageDTO<Product> pageDTO = new PageDTO<>(pageProducts);
        List<Product> products = ObjectToViewTransformUtil.transformToView(Views.ProductRaw.class, pageProducts.getContent());
        pageDTO.setContent(products);
        List<PropertyOptionsDTO> productPropertyChoices;
        List<PropertyOptionsDTO> productFieldChoices;
        List<PropertyOptionsDTO> propertyChoices = new ArrayList<>();
        if (isNeedPropertyChoices) {
            productPropertyChoices = productPropertyService.getProductPropertyOptionsDTO(category);
            productFieldChoices = productService.getProductFieldOptionsDTO(category);
            propertyChoices = Stream.concat(productPropertyChoices.stream(), productFieldChoices.stream())
                    .collect(Collectors.toList());
        }
        Map<String, Object> frontendData = new HashMap<>();
        frontendData.put("category", category);
        frontendData.put("page", pageDTO);
        frontendData.put("propertyChoices", propertyChoices);
        frontendData.put("isNeedPropertyChoices", isNeedPropertyChoices);
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(gsonExcludeStrategies.getCategoryWithoutPropertiesAndProducts())
                .addSerializationExclusionStrategy(gsonExcludeStrategies.getPropertyWithoutCategory())
                .create();
        return gson.toJson(frontendData);
    }

    private boolean isOptionsAbsent(List<PropertyOptionsDTO> activeProductPropertyOptions, List<PropertyOptionsDTO> activeProductFieldsOptions) {
        return (activeProductPropertyOptions == null || activeProductPropertyOptions.isEmpty())
                && (activeProductFieldsOptions == null || activeProductFieldsOptions.isEmpty());
    }

    private List<PropertyOptionsDTO> getActiveProductFieldsOptions(MultiValueMap<String, String> queryMap) {
        List<PropertyOptionsDTO> activeOptions = new ArrayList<>();
        List<Property> properties = propertyByProductFieldsService.getPropertyByProductFields();
        List<String> propertyPaths = properties.stream().map(Property::getPath).collect(Collectors.toList());
        for (String key : queryMap.keySet()) {
            if (isProductFieldsKey(key)) {
                int indexProperty = propertyPaths.indexOf(key);
                Property property = properties.get(indexProperty);
                List<String> queryPropertyPaths = queryMap.get(key);
                Set<String> queryPropertyPathsSet = new HashSet<>(queryPropertyPaths);
                if (!isMinMaxProperty(queryPropertyPaths)) {
                    Set<PropertyData> propertyChoices = getPropertyData(queryPropertyPathsSet, property);
                    activeOptions.add(new PropertyOptionsDTO(property, propertyChoices, null, null));
                } else {
                    PropertyData min = getMinOptionPropertyData(queryPropertyPaths, property.getType());
                    PropertyData max = getMaxOptionPropertyData(queryPropertyPaths, property.getType());
                    activeOptions.add(new PropertyOptionsDTO(property, null, min, max));
                }
            }
        }
        return activeOptions;
    }

    private PropertyData getMaxOptionPropertyData(List<String> queryPropertyPaths, PropertyType type) {
        PropertyData propertyData = new PropertyData();
        if (type == PropertyType.DOUBLE) {
            propertyData.setDoubleData(Double.parseDouble(queryPropertyPaths.get(2)));
        } else if (type == PropertyType.INTEGER) {
            propertyData.setIntegerData(Integer.parseInt(queryPropertyPaths.get(2)));
        }
        return propertyData;
    }

    private PropertyData getMinOptionPropertyData(List<String> queryPropertyPaths, PropertyType type) {
        PropertyData propertyData = new PropertyData();
        if (type == PropertyType.DOUBLE) {
            propertyData.setDoubleData(Double.parseDouble(queryPropertyPaths.get(1)));
        } else if (type == PropertyType.INTEGER) {
            propertyData.setIntegerData(Integer.parseInt(queryPropertyPaths.get(1)));
        }
        return propertyData;
    }

    private Set<PropertyData> getPropertyData(Set<String> queryPropertyPaths, Property property) {
        switch (property.getType()) {
            case STRING:
                return queryPropertyPaths.stream().map(x -> {
                    PropertyData propertyData = new PropertyData();
                    propertyData.setName(x);
                    return propertyData;
                }).collect(Collectors.toSet());
            case DOUBLE:
                return queryPropertyPaths.stream().map(x -> {
                    PropertyData propertyData = new PropertyData();
                    propertyData.setDoubleData(Double.parseDouble(x));
                    return propertyData;
                }).collect(Collectors.toSet());
            case INTEGER:
                return queryPropertyPaths.stream().map(x -> {
                    PropertyData propertyData = new PropertyData();
                    propertyData.setIntegerData(Integer.parseInt(x));
                    return propertyData;
                }).collect(Collectors.toSet());
            case BOOLEAN:
                return queryPropertyPaths.stream().map(x -> {
                    PropertyData propertyData = new PropertyData();
                    propertyData.setBooleanData(Boolean.parseBoolean(x));
                    return propertyData;
                }).collect(Collectors.toSet());
            default:
                throw new RuntimeException("Unknown property type: " + property.getType());
        }
    }

    private List<PropertyOptionsDTO> getActivePropertyOptions(Category category, MultiValueMap<String, String> queryMap) {
        List<PropertyOptionsDTO> activeOptions = new ArrayList<>();
        List<Property> properties = category.getProperties();
        List<String> propertyPaths = properties.stream().map(Property::getPath).collect(Collectors.toList());
        for (String key : queryMap.keySet()) {
            if (isPropertyKey(key)) {
                int indexProperty = propertyPaths.indexOf(key);
                Property property = properties.get(indexProperty);
                List<String> queryPropertyPaths = queryMap.get(key);
                if (!isMinMaxProperty(queryPropertyPaths)) {
                    Set<PropertyData> propertyChoices = queryPropertyPaths.stream().map(x -> urlEncodingASCIIStringReplacementService.replace(x)).map(path -> new PropertyData(path)).collect(Collectors.toSet());
                    activeOptions.add(new PropertyOptionsDTO(property, propertyChoices, null, null));
                } else {
                    PropertyData min = getMinOptionPropertyData(queryPropertyPaths, property.getType());
                    PropertyData max = getMaxOptionPropertyData(queryPropertyPaths, property.getType());
                    activeOptions.add(new PropertyOptionsDTO(property, null, min, max));
                }
            }
        }
        return activeOptions;
    }

    private boolean isMinMaxProperty(List<String> propertyPaths) {
        return MIN_MAX_MARK.equals(propertyPaths.get(0));
    }

    private boolean isPropertyKey(String key) {
        return !urlReservedWordsCommonService.isReserved(key) && !urlReservedWordsProductFieldsService.isReserved(key);
    }

    private boolean isProductFieldsKey(String key) {
        return urlReservedWordsProductFieldsService.isReserved(key);
    }
}
