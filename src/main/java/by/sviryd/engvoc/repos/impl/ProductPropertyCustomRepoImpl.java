package by.sviryd.engvoc.repos.impl;

import by.sviryd.engvoc.domain.*;

import by.sviryd.engvoc.domain.dto.PropertyOptionsDTO;
import by.sviryd.engvoc.repos.ProductPropertyCustomRepo;
import by.sviryd.engvoc.service.JPAUtilService;
import by.sviryd.engvoc.service.stringReplacementService.URLKeysEntityFieldsStringReplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
class ProductPropertyCustomRepoImpl implements ProductPropertyCustomRepo {
    @Autowired
    private URLKeysEntityFieldsStringReplacementService stringReplacementService;
    private final JPAUtilService jpaUtilService;
    private EntityManager entityManager;

    @Autowired
    public ProductPropertyCustomRepoImpl(EntityManagerFactory entityManagerFactory, JPAUtilService jpaUtilService) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.jpaUtilService = jpaUtilService;
    }

    @Override
    public Page<Product> getProducts(List<PropertyOptionsDTO> activeProductPropertyOptions, List<PropertyOptionsDTO> activeProductFieldsOptions, Category category, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = getProductPropertyPredicates(activeProductPropertyOptions, cb, cq, root);
        if(activeProductFieldsOptions != null && !activeProductFieldsOptions.isEmpty()){
            List<Predicate> predicatesProductFields = getProductFieldsPredicates(activeProductFieldsOptions, cb, cq, root);
            predicates.addAll(predicatesProductFields);
        }
        Predicate categoryPredicate = cb.equal(root.get("category"), category);
        predicates.add(categoryPredicate);
        cq.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        setSortToQuery(cq, cb, root, pageable.getSort());
        Long total = jpaUtilService.count(cq);
        TypedQuery<Product> query = entityManager.createQuery(cq);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);
        List<Product> content = query.getResultList();
        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> getProductFieldsPredicates(List<PropertyOptionsDTO> activeProductFieldsOptions, CriteriaBuilder cb, CriteriaQuery<Product> cq, Root<Product> root) {
        List<Predicate> predicates = new ArrayList<>();
        for (PropertyOptionsDTO option : activeProductFieldsOptions) {
            Predicate optionPredicate = getMinMaxProductFieldsPredicate(cb, root, option);
            if (optionPredicate == null) {
                optionPredicate = getOrProductFieldPredicate(cb, root, option);
            }
            predicates.add(optionPredicate);
        }
        return predicates;
    }

    private Predicate getOrProductFieldPredicate(CriteriaBuilder cb, Root<Product> root, PropertyOptionsDTO option) {
        List<Predicate> predicatesOR = new ArrayList<>();
        Property property = option.getProperty();
        String field = stringReplacementService.replace(property.getPath());
        for (PropertyData data : option.getChoices()) {
            Predicate predicate = null;
            switch (property.getType()) {
                case STRING:
                    if(data.getName() != null) {
                        predicate = cb.equal(root.get(field), data.getName());
                    }
                    break;
                case DOUBLE:
                    if(data.getDoubleData() != null) {
                        predicate = cb.equal(root.get(field), data.getDoubleData());
                    }
                    break;
                case INTEGER:
                    if(data.getIntegerData() != null) {
                        predicate = cb.equal(root.get(field), data.getIntegerData());
                    }
                    break;
                case BOOLEAN:
                    if(data.getIntegerData() != null) {
                        predicate = cb.equal(root.get(field), data.isBooleanData());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Cannot resolve PropertyType: " + property.getType());
            }
            if(predicate == null && data.getPath() != null) {
                predicate = cb.equal(root.get(field), data.getPath());
            }
            if (predicate == null) continue;
            predicatesOR.add(predicate);
        }
        return cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
    }

    private Predicate getMinMaxProductFieldsPredicate(CriteriaBuilder cb, Root<Product> root, PropertyOptionsDTO option) {
        Property property = option.getProperty();
        PropertyData min = option.getMin();
        PropertyData max = option.getMax();
        String field = stringReplacementService.replace(property.getPath());
        if (min != null || max != null) {
            Predicate predicateMinData = null;
            Predicate predicateMaxData = null;
            if (min != null) {
                switch (property.getType()) {
                    case DOUBLE:
                        predicateMinData = cb.ge(root.get(field), min.getDoubleData());
                        break;
                    case INTEGER:
                        predicateMinData = cb.ge(root.get(field), min.getIntegerData());
                        break;
                    default:
                        throw new IllegalArgumentException("PropertyChoiceDTO cannot determinate min or max PropertyData if PropertyType not as DOUBLE or INTEGER");
                }
            }
            if (max != null) {
                switch (property.getType()) {
                    case DOUBLE:
                        predicateMaxData = cb.le(root.get(field), max.getDoubleData());
                        break;
                    case INTEGER:
                        predicateMaxData = cb.le(root.get(field), max.getIntegerData());
                        break;
                    default:
                        throw new IllegalArgumentException("PropertyChoiceDTO cannot determinate min or max PropertyData if PropertyType not as DOUBLE or INTEGER");
                }
            }
            return cb.and(predicateMinData, predicateMaxData);
        }
        return null;
    }

    private List<Predicate> getProductPropertyPredicates(List<PropertyOptionsDTO> activePropertyOptions, CriteriaBuilder cb, CriteriaQuery<Product> cq, Root<Product> root) {
        List<Predicate> predicates = new ArrayList<>();
        for (PropertyOptionsDTO option : activePropertyOptions) {
            Subquery<ProductProperty> subquery = cq.subquery(ProductProperty.class);
            Root<ProductProperty> subRoot = subquery.from(ProductProperty.class);
            Predicate optionPredicate = getMinMaxProductPropertyPredicate(cb, subRoot, option);
            if (optionPredicate == null) {
                optionPredicate = getOrProductPropertyPredicate(cb, subRoot, option);
            }
            Predicate on = cb.equal(root.get("id"), subRoot.get("id").get("productId"));
            subquery.select(subRoot.get("id").get("productId"))
                    .distinct(true)
                    .where(cb.and(on, optionPredicate));
            Predicate exists = cb.exists(subquery);
            predicates.add(exists);
        }
        return predicates;
    }

    private Predicate getOrProductPropertyPredicate(CriteriaBuilder cb, Root<ProductProperty> productProperty, PropertyOptionsDTO option) {
        List<Predicate> predicatesOR = new ArrayList<>();
        Property property = option.getProperty();
        for (PropertyData data : option.getChoices()) {
            Predicate predicateData = null;
            switch (property.getType()) {
                case STRING:
                    if(data.getName() != null) {
                        predicateData = cb.equal(productProperty.get("propertyData").get("name"), data.getName());
                    }
                    break;
                case DOUBLE:
                    if(data.getDoubleData() != null) {
                        predicateData = cb.equal(productProperty.get("propertyData").get("doubleData"), data.getDoubleData());
                    }
                    break;
                case INTEGER:
                    if(data.getIntegerData() != null) {
                        predicateData = cb.equal(productProperty.get("propertyData").get("integerData"), data.getIntegerData());
                    }
                    break;
                case BOOLEAN:
                    if(data.getIntegerData() != null) {
                        predicateData = cb.equal(productProperty.get("propertyData").get("booleanData"), data.isBooleanData());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Cannot resolve PropertyType: " + property.getType());
            }
            if(predicateData == null && data.getPath() != null) {
                predicateData = cb.equal(productProperty.get("propertyData").get("path"), data.getPath());
            }
            if (predicateData == null) continue;
            Predicate or = cb.and(cb.equal(productProperty.get("property"), property), predicateData);
            predicatesOR.add(or);
        }
        return cb.or(predicatesOR.toArray(new Predicate[predicatesOR.size()]));
    }

    private Predicate getMinMaxProductPropertyPredicate(CriteriaBuilder cb, Root<ProductProperty> productProperty, PropertyOptionsDTO option) {
        Property property = option.getProperty();
        PropertyData min = option.getMin();
        PropertyData max = option.getMax();
        if (min != null || max != null) {
            Predicate predicateMinData = null;
            Predicate predicateMaxData = null;
            if (min != null) {
                switch (property.getType()) {
                    case DOUBLE:
                        predicateMinData = cb.ge(productProperty.get("propertyData").get("doubleData"), min.getDoubleData());
                        break;
                    case INTEGER:
                        predicateMinData = cb.ge(productProperty.get("propertyData").get("integerData"), min.getIntegerData());
                        break;
                    default:
                        throw new IllegalArgumentException("PropertyChoiceDTO cannot determinate min or max PropertyData if PropertyType not as DOUBLE or INTEGER");
                }
            }
            if (max != null) {
                switch (property.getType()) {
                    case DOUBLE:
                        predicateMaxData = cb.le(productProperty.get("propertyData").get("doubleData"), max.getDoubleData());
                        break;
                    case INTEGER:
                        predicateMaxData = cb.le(productProperty.get("propertyData").get("integerData"), max.getIntegerData());
                        break;
                    default:
                        throw new IllegalArgumentException("PropertyChoiceDTO cannot determinate min or max PropertyData if PropertyType not as DOUBLE or INTEGER");
                }
            }
            return cb.and(cb.equal(productProperty.get("property"), property), predicateMinData, predicateMaxData);
        }
        return null;
    }

    private void setSortToQuery(CriteriaQuery<Product> cq, CriteriaBuilder cb, Root<Product> root, Sort sort) {
        Order[] orders = getOrders(cb, root, sort);
        cq.orderBy(orders);
    }

    private Order[] getOrders(CriteriaBuilder cb, Root<Product> root, Sort sort) {
        List<Order> collect = sort.get().map(order -> {
            Sort.Direction direction = order.getDirection();
            if (direction == Sort.Direction.ASC) {
                return cb.asc(root.get(order.getProperty()));
            } else {
                return cb.desc(root.get(order.getProperty()));
            }
        }).collect(Collectors.toList());
        return collect.toArray(new Order[0]);
    }
}