package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category,Long>, CategoryCustomRepo {
    Optional<Category> findByName(String name);
    Optional<Category> findByNameAndParent(String name, Long parent);
}
