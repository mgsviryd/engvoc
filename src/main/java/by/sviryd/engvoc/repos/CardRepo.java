package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepo extends JpaRepository<Card, Long>, CardCustomRepo {

    @Query("select d from Card d where d.dictionary = :dictionary")
    Page<Card> getCardsByDictionary(@Param("dictionary") Dictionary dictionary, Pageable pageable);

    @Query("select d from Card d where d.dictionary = :dictionary")
    List<Card> getCardsByDictionary(@Param("dictionary") Dictionary dictionary);

    Card findDistinctByWordAndTranslation(String word, String translation);

    void deleteByDictionary(Dictionary dictionary);
    void deleteByDictionaryIn(List<Dictionary>dictionaries);
    void deleteByIdIn(List<Long> ids);
}
