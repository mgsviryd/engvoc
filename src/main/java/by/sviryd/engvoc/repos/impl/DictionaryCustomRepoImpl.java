package by.sviryd.engvoc.repos.impl;

import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.repos.DictionaryCustomRepo;
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
class DictionaryCustomRepoImpl implements DictionaryCustomRepo {
    @Autowired
    private final JPAUtilService jpaUtilService;
    private final EntityManager entityManager;

    @Autowired
    public DictionaryCustomRepoImpl(EntityManagerFactory entityManagerFactory, JPAUtilService jpaUtilService) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.jpaUtilService = jpaUtilService;
    }

    @Override
    public List<Dictionary> findDistinctByNameAndParent(List<Dictionary> dictionaries) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dictionary> cq = cb.createQuery(Dictionary.class);
        Root<Dictionary> root = cq.from(Dictionary.class);
        List<Predicate> predicatesOR = new ArrayList<>();
        for (int i = 0; i < dictionaries.size(); i++) {
            Predicate word = cb.equal(root.get("name"), dictionaries.get(i).getName());
            Predicate translation = cb.equal(root.get("parent"), dictionaries.get(i).getParent());
            Predicate or = cb.and(word, translation);
            predicatesOR.add(or);
        }
        Predicate or = cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
        cq.select(root).where(or).distinct(true);
        TypedQuery<Dictionary> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}