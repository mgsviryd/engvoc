package by.sviryd.engvoc.repos.impl;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.repos.CardCustomRepo;
import by.sviryd.engvoc.service.JPAUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
class CardCustomRepoImpl implements CardCustomRepo {
    @Autowired
    private final JPAUtilService jpaUtilService;
    private final EntityManager entityManager;

    @Autowired
    public CardCustomRepoImpl(EntityManagerFactory entityManagerFactory, JPAUtilService jpaUtilService) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.jpaUtilService = jpaUtilService;
    }

    @Override
    public Card findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(Card card, User client) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Card> cq = cb.createQuery(Card.class);
        Root<Card> root = cq.from(Card.class);
        List<Predicate> predicatesOR = new ArrayList<>();
        Predicate pClient = cb.equal(root.get("client"), client);
        Predicate pWord = cb.equal(root.get("word"), card.getWord());
        Predicate pTranslation = cb.equal(root.get("translation"), card.getTranslation());
        Predicate pUnrepeatedTrue = cb.equal(root.get("unrepeated"), true);
        Predicate and = cb.and(pClient, pWord, pTranslation, pUnrepeatedTrue);
        predicatesOR.add(and);
        Predicate or = cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
        cq.select(root).where(or).distinct(true);
        TypedQuery<Card> query = entityManager.createQuery(cq);
        List<Card> resultList = query.getResultList();
        if (resultList.isEmpty()) return null;
        return resultList.get(0);
    }

    @Override
    public List<Card> findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(List<Card> cards, User client) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Card> cq = cb.createQuery(Card.class);
        Root<Card> root = cq.from(Card.class);
        List<Predicate> predicatesOR = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            Predicate pClient = cb.equal(root.get("client"), client);
            Predicate pWord = cb.equal(root.get("word"), cards.get(i).getWord());
            Predicate pTranslation = cb.equal(root.get("translation"), cards.get(i).getTranslation());
            Predicate pUnrepeatedTrue = cb.equal(root.get("unrepeated"), true);
            Predicate and = cb.and(pClient, pWord, pTranslation, pUnrepeatedTrue);
            predicatesOR.add(and);
        }
        Predicate or = cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
        cq.select(root).where(or).distinct(true);
        TypedQuery<Card> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    public List<Card> findDistinctByClientAndVocabularyAndWordAndTranslationWithUnrepeatedTrueAndLearnedFalse(List<Card> cards, User client, Vocabulary vocabulary) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Card> cq = cb.createQuery(Card.class);
        Root<Card> root = cq.from(Card.class);
        List<Predicate> predicatesOR = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            Predicate pClient = cb.equal(root.get("client"), client);
            Predicate pVocabulary = cb.equal(root.get("vocabulary"), vocabulary);
            Predicate pWord = cb.equal(root.get("word"), cards.get(i).getWord());
            Predicate pTranslation = cb.equal(root.get("translation"), cards.get(i).getTranslation());
            Predicate pUnrepeatedTrue = cb.equal(root.get("unrepeated"), true);
            Predicate pLearnedFalse = cb.equal(root.get("learned"), false);
            Predicate and = cb.and(pClient, pVocabulary, pWord, pTranslation, pUnrepeatedTrue, pLearnedFalse);
            predicatesOR.add(and);
        }
        Predicate or = cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
        cq.select(root).where(or).distinct(true);
        TypedQuery<Card> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}