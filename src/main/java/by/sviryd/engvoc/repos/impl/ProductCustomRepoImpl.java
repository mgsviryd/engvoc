package by.sviryd.engvoc.repos.impl;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Product;
import by.sviryd.engvoc.repos.ProductCustomRepo;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
public class ProductCustomRepoImpl implements ProductCustomRepo {
    private EntityManager entityManager;

    @Autowired
    public ProductCustomRepoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public List<Object[]> getProductFieldsData(Category category, List<String> fields) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(Product.class);
        ProjectionList projectionList = Projections.projectionList();
        for (String field : fields) {
            projectionList.add(Projections.distinct(Projections.property(field)));
        }
        criteria.setProjection(projectionList);
        criteria.add(Restrictions.eq("category_id", category.getId()));
        return criteria.list();
    }
}
