package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;

import java.util.List;

public interface CardCustomRepo {
    List<Card> findDistinctByWordAndTranslation(List<Card> cards);
}
