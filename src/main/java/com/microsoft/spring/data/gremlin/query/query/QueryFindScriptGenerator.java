/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.script.AbstractGremlinScriptLiteral;
import com.microsoft.spring.data.gremlin.query.criteria.Criteria;
import com.microsoft.spring.data.gremlin.query.criteria.CriteriaType;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryFindScriptGenerator implements QueryScriptGenerator {

    private <T> List<String> generateIsEqual(@NonNull Criteria criteria, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        String subject = criteria.getSubject();

        if (subject.equals(info.getIdField().getName())) {
            subject = Constants.PROPERTY_ID; // If subject is @Id/id field, use id property in database.
        }

        final List<String> scriptList = new ArrayList<>();

        if (info.isEntityVertex()) {
            scriptList.add(Constants.GREMLIN_PRIMITIVE_VERTEX_ALL);
        } else if (info.isEntityEdge()) {
            scriptList.add(Constants.GREMLIN_PRIMITIVE_EDGE_ALL);
        } else {
            throw new UnsupportedOperationException("Cannot generate script from graph entity");
        }

        Assert.isTrue(criteria.getSubValues().size() == 1, "should be only one value for IS_EQUAL");

        scriptList.add(AbstractGremlinScriptLiteral.generateHas(Constants.PROPERTY_LABEL, info.getEntityLabel()));
        scriptList.add(AbstractGremlinScriptLiteral.generateHas(subject, criteria.getSubValues().get(0)));

        return scriptList;
    }

    private <T> List<String> generateTraversal(@NonNull GremlinQuery query, @NonNull Class<T> domainClass) {
        final Criteria criteria = query.getCriteria();
        final CriteriaType type = criteria.getType();

        switch (type) {
            case IS_EQUAL:
                return this.generateIsEqual(criteria, domainClass);
            // TODO(panli): More keyword will be add in future
            default:
                throw new UnsupportedOperationException("unsupported Criteria type");
        }
    }

    @Override
    public <T> List<String> generate(@NonNull GremlinQuery query, @NonNull Class<T> domainClass) {
        final List<String> scriptList = new ArrayList<>();

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.addAll(this.generateTraversal(query, domainClass));

        return Collections.singletonList(String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList));
    }
}

