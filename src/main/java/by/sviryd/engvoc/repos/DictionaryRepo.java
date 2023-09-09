package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.LangLocalePair;
import by.sviryd.engvoc.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DictionaryRepo extends JpaRepository<Dictionary, UUID>, DictionaryCustomRepo {
    Optional<Dictionary> findByName(String name);

    Optional<Dictionary> findByNameAndUnrepeated(String name, Boolean unrepeated);

    Optional<Dictionary> findByNameAndParent(String name, Long parent);

    List<Dictionary> findAllByAuthorAndPair(User author, LangLocalePair pair);

    void deleteByIdIn(List<UUID> ids);

    void deleteByUnrepeated(boolean unrepeated);

//    @Query("select d from Dictionary d where d.author = :author and d.pair =:pair and d.unrepeated = :unrepeated and d.name = :name")
//    Dictionary findByAuthorAndPairAndUnrepeatedAndName(@Param("author") User author, @Param("pair") LangLocalePair pair, @Param("unrepeated") boolean unrepeated, @Param("name") String name);
    Dictionary findByAuthorAndPairAndUnrepeatedAndName(User author, LangLocalePair pair, boolean unrepeated, String name);

}
