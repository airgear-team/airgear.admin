package com.airgear.admin.specifications;

import lombok.Data;

@Data
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;

    public SearchCriteria(String key, SearchOperation op, Object value) {
        this.key = key;
        this.operation = op;
        this.value = value;
    }

    public boolean isOrPredicate() {
        return orPredicate;
    }
}

