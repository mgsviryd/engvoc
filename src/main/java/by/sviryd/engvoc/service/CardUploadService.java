package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardUploadService {
    @Autowired
    private CardService cardService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private CardUnrepeatedService cardUnrepeatedService;

    public HashMap<Object, Object> saveNewDictionariesAndCards(User client, List<Card> cards, Vocabulary vocabulary) {
        if (cards.isEmpty()) {
            HashMap<Object, Object> data = new HashMap<>();
            data.put("cards", Collections.emptyList());
            data.put("dictionaries", Collections.emptyList());
            return data;
        }
        Set<Dictionary> dictionaries = cards.stream().map(Card::getDictionary).filter(Objects::nonNull).collect(Collectors.toSet());
        LocalDateTime now = LocalDateTime.now();
        dictionaries.forEach(d -> {
            d.setAuthor(client);
            d.setVocabulary(vocabulary);
            d.setUnrepeated(false);
            d.setCreationLDT(now);
        });
        cards.forEach(c -> {
            c.setClient(client);
            c.setUnrepeated(false);
            c.setCreationLDT(now);
        });
        List<Dictionary> dsSaved = dictionaryService.saveAll(new ArrayList<>(dictionaries));
        cards.forEach(c -> c.setDictionary(dsSaved.stream().filter(x -> c.getDictionary().getName().equals(x.getName())).findFirst().get()));
        List<Card> csSaved = cardService.saveAll(cards);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("cards", csSaved);
        data.put("dictionaries", dsSaved);
        return data;
    }

    public HashMap<Object, Object> updateLearnedStatusUnrepeatedCards(User user, List<Card> cards, Vocabulary vocabulary) {
        if (cards.isEmpty()) {
            HashMap<Object, Object> data = new HashMap<>();
            data.put("updateLearnedStatusUnrepeatedCards", Collections.emptyList());
            return data;
        }
        List<Card> cardsLearned = cards.stream().filter(Card::isLearned).collect(Collectors.toList());
        List<Card> cardsForUpdate = cardService.findDistinctByClientAndVocabularyAndWordAndTranslationWithUnrepeatedTrueAndLearnedFalse(cardsLearned, user, vocabulary);
        cardsForUpdate.forEach(Card::makeLearned);
        List<Card> updateLearnedStatusUnrepeatedCards = cardService.saveAll(cardsForUpdate);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("updateLearnedStatusUnrepeatedCards", updateLearnedStatusUnrepeatedCards);
        return data;
    }

    public HashMap<Object, Object> saveNewUnrepeatedCards(User user, List<Card> cards, Vocabulary vocabulary) {
        if (cards.isEmpty()) {
            HashMap<Object, Object> data = new HashMap<>();
            data.put("updateLearnedStatusUnrepeatedCards", Collections.emptyList());
            return data;
        }
        Dictionary newDictionary = dictionaryService.findIfAbsentSaveNewUnrepeated(user, vocabulary);
        cards.forEach(c -> {
            c.setDictionary(newDictionary);
        });
        HashMap<Object, Object> data = new HashMap<>();
        List<Card> repeatedCards = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(cards, user);
        List<Card> newCards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards, repeatedCards);
        newCards.forEach(c -> {
            c.setUnrepeated(true);
        });
        newCards = cardService.saveAll(newCards);
        data.put("saveNewUnrepeatedCards", newCards);
        data.put("saveNewUnrepeatedDictionary", newDictionary);
        return data;
    }

    public HashMap<Object, Object> updateCardsWithAbsentSound(User user, List<Card> cards, Vocabulary vocabulary) {
        if (cards.isEmpty()) {
            HashMap<Object, Object> data = new HashMap<>();
            data.put("updateCardsWithAbsentSound", Collections.emptyList());
            return data;
        }
        List<Card> cardsSound = cards.stream().filter(c -> !Objects.isNull(c.getSound())).collect(Collectors.toList());
        cardsSound = cardUnrepeatedService.getUnrepeatedByWord(cardsSound);
        List<Card> cardsForUpdate = cardService.findByClientAndVocabulary(user, vocabulary);
        cardsForUpdate = cardsForUpdate.stream().filter(c -> Objects.isNull(c.getSound())).collect(Collectors.toList());
        List<Card> finalCardsSound = cardsSound;
        cardsForUpdate.forEach(c -> {
            Optional<Card> card = finalCardsSound.stream().filter(cc -> c.getWord().equals(cc.getWord())).findFirst();
            if (card.isPresent()) {
                String sound = card.get().getSound();
                c.setSound(sound);
            }
        });
        List<Card> updateCardsWithAbsentSound = cardService.saveAll(cardsForUpdate);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("updateCardsWithAbsentSound", updateCardsWithAbsentSound);
        return data;
    }
}
