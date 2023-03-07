package by.sviryd.engvoc.service.search;

import by.sviryd.engvoc.config.HibernateSearchConfig;
import by.sviryd.engvoc.domain.Product;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductSearchService {
    @Autowired
    private HibernateSearchConfig hibernateSearchConfig;
    @Autowired
    private HibernateSearchService searchService;

    public List<Product> searchProductExcessMinGramSize(String text, Pageable pageable) {
        text = text.toLowerCase();
        QueryBuilder queryBuilder = searchService.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Product.class)
                .get();
        Query query = queryBuilder
                .keyword()
                .onFields(
                        "name",
                        "code",
                        "category.name"
                )
                .matching(text)
                .createQuery();
        FullTextQuery jpaQuery
                = searchService.createFullTextQuery(query, Product.class);
        Sort sort = new Sort(
                SortField.FIELD_SCORE,
                new SortField("popular", SortField.Type.STRING, true));
        jpaQuery.setSort(sort);
        jpaQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        jpaQuery.setMaxResults(pageable.getPageSize());
        return jpaQuery.getResultList();
    }

    public List<Product> searchProductInsideMinGramSize(String text, Pageable pageable) {
        text = text.toLowerCase();
        QueryBuilder queryBuilder = searchService.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Product.class)
                .get();
        Query query = queryBuilder
                .keyword()
                .wildcard()
                .onFields(
                        "name",
                        "code",
                        "category.name"
                )
                .matching(text.trim() + hibernateSearchConfig.getRegexAnyChar())
                .createQuery();
        FullTextQuery jpaQuery
                = searchService.createFullTextQuery(query, Product.class);
        Sort sort = new Sort(
                SortField.FIELD_SCORE,
                new SortField("popular", SortField.Type.STRING, true));
        jpaQuery.setSort(sort);
        jpaQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        jpaQuery.setMaxResults(pageable.getPageSize());
        return jpaQuery.getResultList();
    }

    public List<Product> searchProduct(String text, Pageable pageable) {
        text = text.trim();
        if (text.length() == 0) return Collections.emptyList();
        if (text.length() > hibernateSearchConfig.getMinGramSize()) {
            return searchProductExcessMinGramSize(text, pageable);
        } else {
            return searchProductInsideMinGramSize(text, pageable);
        }
    }
}
