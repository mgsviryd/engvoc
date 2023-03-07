package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.repos.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    public Category save(Category category) {
        return categoryRepo.save(category);
    }

    public long count() {
        return categoryRepo.count();
    }

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepo.findAll(pageable);
    }

    public Category saveAndFlush(Category category) {
        return categoryRepo.saveAndFlush(category);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepo.findByName(name);
    }

    //TODO - SQL Error: 1062, SQLState: 23000 in multithread
    public Category saveShaneOfCategory(List<Category> categories) {
        Category parent = null;
        Long parentId = 0L;
        for (Category c : categories) {
            if (parent != null) {
                parentId = parent.getId();
            }
            Optional<Category> optional = findByNameAndParent(c.getName(), parentId);
            if (optional.isPresent()) {
                parent = optional.get();
            } else {
                c.setParent(parentId);
                parent = save(c);
            }
        }
        return parent;
    }

    public Optional<Category> findByNameAndParent(String name, Long parent) {
        return categoryRepo.findByNameAndParent(name, parent);
    }


    public Page<Category> getCategories(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return categoryRepo.findAll(pageable);
    }

    public List<Category> saveAll(List<Category> categories) {
        return categoryRepo.saveAll(categories);
    }

    public List<Category> findAll() {
        return categoryRepo.findAll();
    }
    public List<Category> findAllWithProperties(){
        return categoryRepo.findAllWithProperties();
    }

}
