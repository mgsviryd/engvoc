package by.sviryd.engvoc.repos.impl;

import by.sviryd.engvoc.domain.Card;
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
    public Card findDistinctByWordAndTranslationWithUniqueTrue(Card card) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Card> cq = cb.createQuery(Card.class);
        Root<Card> root = cq.from(Card.class);
        List<Predicate> predicatesOR = new ArrayList<>();
        Predicate word = cb.equal(root.get("word"), card.getWord());
        Predicate translation = cb.equal(root.get("translation"), card.getTranslation());
        Predicate uniqueTrue = cb.equal(root.get("unique"), true);
        Predicate and = cb.and(word, translation, uniqueTrue);
        predicatesOR.add(and);
        Predicate or = cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
        cq.select(root).where(or).distinct(true);
        TypedQuery<Card> query = entityManager.createQuery(cq);
        List<Card> resultList = query.getResultList();
        if (resultList.isEmpty())return null;
        return resultList.get(0);
    }

    @Override
    public List<Card> findDistinctByWordAndTranslationWithUniqueTrue(List<Card> cards) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Card> cq = cb.createQuery(Card.class);
        Root<Card> root = cq.from(Card.class);
        List<Predicate> predicatesOR = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            Predicate word = cb.equal(root.get("word"), cards.get(i).getWord());
            Predicate translation = cb.equal(root.get("translation"), cards.get(i).getTranslation());
            Predicate uniqueTrue = cb.equal(root.get("unique"), true);
            Predicate and = cb.and(word, translation, uniqueTrue);
            predicatesOR.add(and);
        }
        Predicate or = cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
        cq.select(root).where(or).distinct(true);
        TypedQuery<Card> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}