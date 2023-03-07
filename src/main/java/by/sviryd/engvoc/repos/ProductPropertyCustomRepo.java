package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.domain.dto.PropertyOptionsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductPropertyCustomRepo {
    Page<Product> getProducts(List<PropertyOptionsDTO> activeProductPropertyOptions, List<PropertyOptionsDTO> activeProductFieldsOptions, Category category, Pageable pageable);
}
