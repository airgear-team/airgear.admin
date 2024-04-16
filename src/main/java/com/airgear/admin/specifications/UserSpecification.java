package com.airgear.admin.specifications;

import com.airgear.admin.model.Goods;
import com.airgear.admin.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {
    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(
            Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if(criteria.getKey().equals("countGoods")){
            return getPredicateCount(root, query, builder);
        }
        else {
            return switch (criteria.getOperation()) {
                case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
                case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
                case GREATER_THAN -> builder.greaterThan(root.get(
                        criteria.getKey()), criteria.getValue().toString());
                case LESS_THAN -> builder.lessThan(root.get(
                        criteria.getKey()), criteria.getValue().toString());
                case LIKE -> builder.like(root.get(
                        criteria.getKey()), criteria.getValue().toString());
                case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
                case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
                case CONTAINS -> builder.like(root.get(
                        criteria.getKey()), "%" + criteria.getValue() + "%");
            };
        }
    }

    private Predicate getPredicateCount(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Subquery<Long> countSubQuery = query.subquery(Long.class);
        Root<User> userRoot2 = countSubQuery.correlate(root);
        Join<User, Goods> goods = userRoot2.join("goods", JoinType.LEFT);
        countSubQuery.select(builder.count(goods));

        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(countSubQuery, criteria.getValue());
            case NEGATION -> builder.notEqual(countSubQuery, criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(countSubQuery, Long.valueOf((String) criteria.getValue()));
            case LESS_THAN -> builder.lessThan(countSubQuery, Long.valueOf((String) criteria.getValue()));
            case LIKE, STARTS_WITH,ENDS_WITH,CONTAINS ->null;
        };
    }

}
