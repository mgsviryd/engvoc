package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Card;

import java.util.List;

public interface CardCustomRepo {
    List<Card> findDistinctByWordAndTranslationWithUniqueTrue(List<Card> cards);
}
