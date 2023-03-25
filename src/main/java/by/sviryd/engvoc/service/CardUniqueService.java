package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Card;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardUniqueService {
    public List<Card> getRepeatedByWordAndTranslation(List<Card> cards, List<Card> source){
        return cards.stream().filter(x -> source.stream().anyMatch(y -> y.getWord().equals(x.getWord()) && y.getTranslation().equals(x.getTranslation()))).collect(Collectors.toList());
    }

    public List<Card> getRepeatedByWordAndTranslation(List<Card> cards){
        return cards.stream().filter(x -> cards.stream().anyMatch(y -> y!=x && y.getWord().equals(x.getWord()) && y.getTranslation().equals(x.getTranslation()))).collect(Collectors.toList());
    }
}
