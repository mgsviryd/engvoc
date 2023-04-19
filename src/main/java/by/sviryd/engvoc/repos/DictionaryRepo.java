package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DictionaryRepo extends JpaRepository<Dictionary, Long>, DictionaryCustomRepo {
    Optional<Dictionary> findByName(String name);
    Optional<Dictionary> findByNameAndUnique(String name, Boolean unique);
    Optional<Dictionary> findByNameAndParent(String name, Long parent);
    void deleteByIdIn(List<Long> ids);
    void deleteByUnique(boolean unique);
}
