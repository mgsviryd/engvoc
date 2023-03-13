package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictionaryRepo extends JpaRepository<Dictionary, Long> {
    Optional<Dictionary> findByName(String name);

    Optional<Dictionary> findByNameAndParent(String name, Long parent);
}
