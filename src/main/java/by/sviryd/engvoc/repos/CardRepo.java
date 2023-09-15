package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CardRepo extends JpaRepository<Card, UUID>, PagingAndSortingRepository<Card, UUID>, CardCustomRepo {

    Page<Card> findAllByDictionary(Dictionary dictionary, Pageable pageable);
    List<Card> findByClientAndVocabulary(User client, Vocabulary vocabulary);

    @Query("select d from Card d where d.dictionary.id in :ids")
    List<Card> getCardsByDictionaryIdIn(List<UUID> ids);


    @Modifying
    @Query("update Card c set c.unrepeated = :unrepeated, c.dictionary = :dictionary where c.id = :id")
    int updateDictionaryAndUnrepeatedById(UUID id, Dictionary dictionary, boolean unrepeated);

    @Modifying
    @Query("update Card c set c.learned = :learned, c.learnedLDT= :learnedLDT where c.id in :ids")
    int updateLearnedAndLearnedLDTByIdIn(List<UUID> ids, boolean learned, LocalDateTime learnedLDT);

    @Modifying
    @Query("update Card c set c.unrepeated = :unrepeated, c.dictionary= :dictionary where c.id in :ids")
    int updateDictionaryAndUnrepeatedByIdIn(List<UUID> ids, Dictionary dictionary, boolean unrepeated);

    void deleteByDictionary(Dictionary dictionary);

    void deleteByDictionaryIn(List<Dictionary> dictionaries);

    void deleteByIdIn(List<UUID> ids);

    List<Card> findAllByClientAndVocabulary(User client, Vocabulary vocabulary);

    List<Card> findAllByDictionary(Dictionary dictionary);
}
