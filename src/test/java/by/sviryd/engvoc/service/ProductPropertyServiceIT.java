package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.domain.dto.PropertyOptionsDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductPropertyServiceIT {
    @Autowired
    private ProductPropertyService productPropertyService;
    @Autowired
    private CategoryHierarchyService categoryCategoryHierarchyService;

    @Test
    public void getPropertyChoicesDTO() throws Exception {
        List<Category> ancestors = categoryCategoryHierarchyService.getParents(1837L);
        List<String> paths = categoryCategoryHierarchyService.transformToPaths(ancestors);
        Category category = categoryCategoryHierarchyService.getCategoryByPaths(paths);
        List<PropertyOptionsDTO> propertyOptionsDTO = productPropertyService.getProductPropertyOptionsDTO(category);
        PropertyOptionsDTO propertyChoice = propertyOptionsDTO.get(0);
        Page<Product> products = productPropertyService.getProducts(Collections.singletonList(propertyChoice), null, category, PageRequest.of(1, 50));
        products.getContent().stream().forEach(System.out::println);
    }
}