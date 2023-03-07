package by.sviryd.engvoc.repos.impl;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.repos.CategoryCustomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class CategoryCustomRepoImpl implements CategoryCustomRepo {
    private EntityManager entityManager;

    @Autowired
    public CategoryCustomRepoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public List<Category> findAllWithProperties() {
        EntityGraph<Category> entityGraph = entityManager.createEntityGraph(Category.class);
        entityGraph.addAttributeNodes("properties");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> rootEntry = cq.from(Category.class);
        CriteriaQuery<Category> all = cq.select(rootEntry);
        TypedQuery<Category> typedQuery = entityManager.createQuery(all);
        typedQuery.setHint("javax.persistence.loadgraph", entityGraph);
        return typedQuery.getResultList();
    }
}
