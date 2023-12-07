package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DictionaryRepo extends JpaRepository<Dictionary, UUID>, DictionaryCustomRepo {
    Optional<Dictionary> findByName(String name);

    Optional<Dictionary> findByNameAndUnrepeated(String name, Boolean unrepeated);

    Optional<Dictionary> findByNameAndParent(String name, Long parent);

    List<Dictionary> findAllByAuthorAndVocabulary(User author, Vocabulary vocabulary);

    void deleteByIdIn(List<UUID> ids);

    void deleteByAuthorAndUnrepeated(User author, boolean unrepeated);
    void deleteByAuthorAndVocabulary(User author, Vocabulary vocabulary);

    Dictionary findByAuthorAndVocabularyAndUnrepeatedAndName(User author, Vocabulary vocabulary, boolean unrepeated, String name);
}
