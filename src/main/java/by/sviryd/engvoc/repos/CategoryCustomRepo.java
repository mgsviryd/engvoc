package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Category;

import java.util.List;

public interface CategoryCustomRepo {
    List<Category> findAllWithProperties();
}
