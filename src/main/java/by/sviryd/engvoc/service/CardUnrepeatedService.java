package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Card;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Service
public class CardUnrepeatedService {
    private static final Comparator<Card> CARD_COMPARATOR_BY_WORD_THEN_TRANSLATION = Comparator.comparing(Card::getWord).thenComparing(Card::getTranslation);
    private static final Comparator<Card> CARD_COMPARATOR_BY_WORD = Comparator.comparing(Card::getWord);

    public List<Card> getRepeatedByWordAndTranslation(List<Card> cards, List<Card> source){
        Set<Card> set= new ConcurrentSkipListSet<>(CARD_COMPARATOR_BY_WORD_THEN_TRANSLATION);
        set.addAll(source);
        return cards.stream().filter(c-> !set.add(c)).collect(Collectors.toList());
    }
    public List<Card> getUnrepeatedByWordAndTranslation(List<Card> cards, List<Card> source){
        Set<Card> set= new ConcurrentSkipListSet<>(CARD_COMPARATOR_BY_WORD_THEN_TRANSLATION);
        set.addAll(source);
        return cards.stream().filter(c-> set.add(c)).collect(Collectors.toList());
    }

    public List<Card> getRepeatedByWordAndTranslation(List<Card> cards){
        Set<Card> set= new ConcurrentSkipListSet<>(CARD_COMPARATOR_BY_WORD_THEN_TRANSLATION);
        return cards.stream().filter(c-> !set.add(c)).collect(Collectors.toList());
    }
    public List<Card> getUnrepeatedByWordAndTranslation(List<Card> cards){
        Set<Card> set= new ConcurrentSkipListSet<>(CARD_COMPARATOR_BY_WORD_THEN_TRANSLATION);
        set.addAll(cards);
        return new ArrayList<>(set);
    }
    public List<Card> getRepeatedByWord(List<Card> cards){
        Set<Card> set= new ConcurrentSkipListSet<>(CARD_COMPARATOR_BY_WORD);
        return cards.stream().filter(c-> !set.add(c)).collect(Collectors.toList());
    }
    public List<Card> getUnrepeatedByWord(List<Card> cards){
        Set<Card> set= new ConcurrentSkipListSet<>(CARD_COMPARATOR_BY_WORD);
        set.addAll(cards);
        return new ArrayList<>(set);
    }
}
