package by.sviryd.engvoc.service;

import by.sviryd.engvoc.component.Hierarchy;
import by.sviryd.engvoc.component.IHierarchy;
import by.sviryd.engvoc.domain.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CategoryHierarchyService implements IHierarchy<Category> {
    private static final Comparator<Category> comparator = Comparator.comparing(Category::getPriority, Comparator.nullsFirst(Comparator.naturalOrder()));
    private static final Predicate<Category> isVisible = x -> !x.isInvisible();
    private static final List<Predicate<Category>> filters = new ArrayList<>();

    static {
        filters.add(isVisible);
    }

    @Autowired
    private CategoryService categoryService;

    private IHierarchy<Category> categoryHierarchy;

    @Override
    public List<Long> getChildsIds(Long id) {
        return categoryHierarchy.getChildsIds(id);
    }

    @Override
    public List<Category> getParents(Long id) {
        return categoryHierarchy.getParents(id);
    }

    @Override
    public List<Category> getChilds(Long id) {
        return categoryHierarchy.getChilds(id);
    }

    @Override
    public List<Long> getParentsIds(Long id) {
        return categoryHierarchy.getParentsIds(id);
    }


    @Override
    public List<Long> getRootIds() {
        return categoryHierarchy.getRootIds();
    }

    @Override
    public Map<Long, List<Long>> getChildMapIds() {
        return categoryHierarchy.getChildMapIds();
    }


    @Override
    public Map<Long, List<Long>> getParentMapIds() {
        return categoryHierarchy.getParentMapIds();
    }

    public List<String> transformToPaths(List<Category> categories) {
        return categories.stream().map(Category::getPath).collect(Collectors.toList());
    }

    public Category getCategoryByPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return null;
        }
        Optional<Category> optional = getAll().stream().filter(x -> x.getPath().equals(paths.get(0)) && x.getParent() == 0).findFirst();
        Category category;
        if (optional.isPresent()) {
            category = optional.get();
            for (int i = 1; i < paths.size() && category != null; i++) {
                int index = i;
                Category curr = category;
                optional = getAll().stream().filter(x -> x.getPath().equals(paths.get(index)) && x.getParent().equals(curr.getId())).findFirst();
                category = optional.orElse(null);
            }
            return category;
        } else {
            return null;
        }
    }

    @Override
    public Map<Long, Category> getMap() {
        return categoryHierarchy.getMap();
    }

    @Override
    public List<Category> getAll() {
        return categoryHierarchy.getAll();
    }

    @Override
    public Category getById(Long id) {
        return categoryHierarchy.getById(id);
    }

    @PostConstruct
//    @Scheduled(fixedDelayString = "${scheduled.HierarchyService.build.fixedDelayInMilliseconds}", initialDelayString = "${scheduled.HierarchyService.build.initialDelayInMilliseconds}")
    public void init() {
        List<Category> all = categoryService.findAllWithProperties();
        categoryHierarchy = new Hierarchy<>(all, comparator, filters);
    }
}
