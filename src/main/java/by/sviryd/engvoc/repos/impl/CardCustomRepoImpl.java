package by.sviryd.engvoc.repos.impl;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.repos.CardCustomRepo;
import by.sviryd.engvoc.service.JPAUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
    public List<Card> findDistinctByWordAndTranslation(List<Card> cards) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Card> cq = cb.createQuery(Card.class);
        Root<Card> root = cq.from(Card.class);
        List<Predicate> predicatesOR = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            Predicate word = cb.equal(root.get("word"), cards.get(i).getWord());
            Predicate translation = cb.equal(root.get("translation"), cards.get(i).getTranslation());
            Predicate and = cb.and(word, translation);
            predicatesOR.add(and);
        }
        Predicate or = cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
        cq.select(root).where(or).distinct(true);
        TypedQuery<Card> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}