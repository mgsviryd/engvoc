package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.service.search.CategorySearchService;
import by.sviryd.engvoc.service.search.ProductSearchService;
import by.sviryd.engvoc.util.ObjectToViewTransformUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    private CategorySearchService categorySearchService;
    @Autowired
    private ProductSearchService productSearchService;

    @GetMapping("category")
    @JsonView(Views.IdNamePathIconProductCount.class)
    public List<Category> searchCategory(
            @RequestParam String text,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return categorySearchService.searchCategory(text, pageable);
    }

    @GetMapping("product")
    @JsonView(Views.ProductRaw.class)
    public List<Product> searchProduct(
            @RequestParam String text,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        return productSearchService.searchProduct(text, pageable);
    }

    @GetMapping("category-product")
    public String searchCategoryAndProduct(
            @RequestParam String text,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        List<Product> products = productSearchService.searchProduct(text, pageable);
        products = ObjectToViewTransformUtil.transformToView(Views.ProductRaw.class, products);
        List<Category> categories = categorySearchService.searchCategory(text, pageable);
        categories = ObjectToViewTransformUtil.transformToView(Views.IdNamePathIconProductCount.class, categories);
        Map<String, Object> frontendData = new HashMap<>();
        frontendData.put("searchProducts", products);
        frontendData.put("searchCategories", categories);
        Gson gson = new Gson();
        return gson.toJson(frontendData);
    }
}
