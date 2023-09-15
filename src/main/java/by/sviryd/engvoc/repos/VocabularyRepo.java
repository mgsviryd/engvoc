package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VocabularyRepo extends JpaRepository<Vocabulary, UUID> {

}
