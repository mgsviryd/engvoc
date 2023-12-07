package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.repos.CardRepo;
import by.sviryd.engvoc.repos.exception.UpdateAllOrNothingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class CardService {
    @Autowired
    private CardRepo cardRepo;

    public long count() {
        return cardRepo.count();
    }

    public void delete(Card card) {
        cardRepo.delete(card);
    }

    public List<Card> findAll() {
        return cardRepo.findAll();
    }

    public Card save(Card card) {
        return cardRepo.save(card);
    }

    public List<Card> saveAll(List<Card> cards) {
        return cardRepo.saveAll(cards);
    }

    public Page<Card> getCards(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return cardRepo.findAll(pageable);
    }

    public Page<Card> findAllByDictionary(Dictionary dictionary, Pageable pageable) {
        return cardRepo.findAllByDictionary(dictionary, pageable);
    }

    public List<Card> findAllByDictionary(Dictionary dictionary) {
        return cardRepo.findAllByDictionary(dictionary);
    }

    public Page<Card> findAll(Pageable pageable) {
        return cardRepo.findAll(pageable);
    }

    public List<Card> findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(List<Card> cards, User client) {
        return cardRepo.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(cards, client);
    }

    public List<Card> findByClientAndVocabulary(User client, Vocabulary vocabulary) {
        return cardRepo.findByClientAndVocabulary(client, vocabulary);
    }
    public List<Card> findDistinctByClientAndVocabularyAndWordAndTranslationWithUnrepeatedTrueAndLearnedFalse(List<Card> cards, User client, Vocabulary vocabulary) {
        return cardRepo.findDistinctByClientAndVocabularyAndWordAndTranslationWithUnrepeatedTrueAndLearnedFalse(cards, client, vocabulary);
    }

    public Card findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(Card card, User client) {
        return cardRepo.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(card, client);
    }

    public void deleteByDictionary(Dictionary dictionary) {
        cardRepo.deleteByDictionary(dictionary);
    }

    public void deleteByDictionaryIn(List<Dictionary> dictionaries) {
        cardRepo.deleteByDictionaryIn(dictionaries);
    }
    public void deleteByClientAndVocabulary(User client, Vocabulary vocabulary) {
        cardRepo.deleteByClientAndVocabulary(client, vocabulary);
    }
    public void deleteByClientAndVocabularyAndUnrepeated(User client, Vocabulary vocabulary, boolean unrepeated) {
        cardRepo.deleteByClientAndVocabularyAndUnrepeated(client, vocabulary, unrepeated);
    }

    public void deleteByIdIn(List<UUID> ids) {
        cardRepo.deleteByIdIn(ids);
    }

    public List<Card> findAllById(List<UUID> ids) {
        return cardRepo.findAllById(ids);
    }

    public List<Card> findAllByClientAndVocabulary(User client, Vocabulary vocabulary) {
        return cardRepo.findAllByClientAndVocabulary(client, vocabulary);
    }

    public Optional<Card> findById(UUID id) {
        return cardRepo.findById(id);
    }

    @Transactional(rollbackFor = {UpdateAllOrNothingException.class})
    public int updateDictionaryAndUnrepeatedById(UUID id, Dictionary dictionary, boolean unrepeated) throws UpdateAllOrNothingException {
        int count = cardRepo.updateDictionaryAndUnrepeatedById(id, dictionary, unrepeated);
        if (count != 1) throw new UpdateAllOrNothingException();
        return count;
    }

    @Transactional(rollbackFor = {UpdateAllOrNothingException.class})
    public int updateDictionaryAndUnrepeatedByIdIn(List<UUID> ids, Dictionary dictionary, boolean unrepeated) throws UpdateAllOrNothingException {
        int count = cardRepo.updateDictionaryAndUnrepeatedByIdIn(ids, dictionary, unrepeated);
        if (count != ids.size()) throw new UpdateAllOrNothingException();
        return count;
    }

    public List<Card> getCardsByDictionaryIdIn(List<UUID> ids) {
        return cardRepo.getCardsByDictionaryIdIn(ids);
    }

    public int updateLearnedAndLearnedLDTByIdIn(List<UUID> ids, boolean learned, LocalDateTime learnedLDT) {
        return cardRepo.updateLearnedAndLearnedLDTByIdIn(ids, learned, learnedLDT);
    }
}
