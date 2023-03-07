package by.sviryd.engvoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class JPAUtilService {
    private EntityManager entityManager;

    @Autowired
    public JPAUtilService(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public <T> Long count(CriteriaQuery<T> cq) {
        return executeCountQuery(getCountQuery(cq));
    }

    public Long executeCountQuery(TypedQuery<Long> query) {
        if (query == null) throw new IllegalArgumentException("Query cannot be null.");
        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    public <T> TypedQuery<Long> getCountQuery(CriteriaQuery<T> cq) {
        Class<T> domainClass = cq.getResultType();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<T> root;
        if (cq.getRestriction() != null) {
            countCq.where(cq.getRestriction());
        }
        if (cq.getGroupRestriction() != null) {
            countCq.having(cq.getGroupRestriction());
        }
        if (cq.getRoots().isEmpty()) {
            root = countCq.from(domainClass);
        } else {
            countCq.getRoots().addAll(cq.getRoots());
            root = (Root<T>) countCq.getRoots().iterator().next();
        }
        countCq.groupBy(cq.getGroupList());
        if (cq.isDistinct()) {
            countCq.select(cb.countDistinct(root));
        } else {
            countCq.select(cb.count(root));
        }
        return entityManager.createQuery(countCq);
    }
}
