package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.service.CategoryHierarchyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductPropertyRepoIT {
    @Autowired
    private ProductPropertyRepo productPropertyRepo;
    @Autowired
    private CategoryHierarchyService categoryCategoryHierarchyService;

    String[] categoryPathsArray = {};
    private List<String> categoryPaths = Arrays.asList(categoryPathsArray);


    @Test
    public List<?> getPropertyPropertyData() throws Exception {
        Category category = categoryCategoryHierarchyService.getCategoryByPaths(categoryPaths);
        List<Property> properties = category.getProperties();
        List<Object[]> data = productPropertyRepo.getProductPropertyData(properties);
        return data;
    }
}