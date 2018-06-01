/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.criteria;

import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Criteria {

    private String subject;
    private List<Object> subValues;
    private final CriteriaType type;
    private final List<Criteria> subCriteria;

    private Criteria(CriteriaType type) {
        this.type = type;
        this.subCriteria = new ArrayList<>();
    }

    public static Criteria getInstance(@NonNull String subject, CriteriaType type, @NonNull List<Object> values) {
        final Criteria criteria = new Criteria(type);

        criteria.subject = subject;
        criteria.subValues = values;

        return criteria;
    }
}
