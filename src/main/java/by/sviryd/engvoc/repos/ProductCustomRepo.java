package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Category;

import java.util.List;

public interface ProductCustomRepo {
    List<Object[]> getProductFieldsData(Category category, List<String> fields);
}
