package com.airgear.admin.specifications;

import com.airgear.admin.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {
    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(
            Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

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
