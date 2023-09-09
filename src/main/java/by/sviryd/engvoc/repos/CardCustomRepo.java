package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.LangLocalePair;
import by.sviryd.engvoc.domain.User;

import java.util.List;

public interface CardCustomRepo {
    Card findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(Card card, User client);

    List<Card> findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(List<Card> cards, User client);

    List<Card> findDistinctByClientAndPairAndWordAndTranslationWithUnrepeatedTrueAndLearnedFalse(List<Card> cards, User client, LangLocalePair pair);
}
