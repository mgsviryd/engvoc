package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.gson.GsonExcludeStrategies;
import by.sviryd.engvoc.service.CategoryHierarchyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/category/hierarchy")
@Transactional
public class CategoryHierarchyRestController {
    @Autowired
    private CategoryHierarchyService categoryCategoryHierarchyService;

    @Autowired
    private GsonExcludeStrategies gsonExcludeStrategies;

    @GetMapping
    public String hierarchy() {
        Map<String, Object> frontendData = new HashMap<>();
        frontendData.put("childMapIds", categoryCategoryHierarchyService.getChildMapIds());
        frontendData.put("parentMapIds", categoryCategoryHierarchyService.getParentMapIds());
        frontendData.put("rootIds", categoryCategoryHierarchyService.getRootIds());
        frontendData.put("map", categoryCategoryHierarchyService.getMap());
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(gsonExcludeStrategies.getCategoryWithoutPropertiesAndProducts()).create();
        return gson.toJson(frontendData);
    }
}
