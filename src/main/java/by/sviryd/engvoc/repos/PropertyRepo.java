package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepo extends JpaRepository<Property,Long>{
    List<Property> findByCategoryAndName(Category category, String name);
}
