package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Card;

import java.util.List;

public interface CardCustomRepo {
    Card findDistinctByWordAndTranslationWithUniqueTrue(Card card);
    List<Card> findDistinctByWordAndTranslationWithUniqueTrue(List<Card> cards);
}
