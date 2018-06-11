/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.script.AbstractGremlinScriptLiteral;
import com.microsoft.spring.data.gremlin.query.criteria.Criteria;
import com.microsoft.spring.data.gremlin.query.criteria.CriteriaType;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.microsoft.spring.data.gremlin.common.Constants.*;

@NoArgsConstructor
public class QueryFindScriptGenerator implements QueryScriptGenerator {

    @Setter(AccessLevel.PRIVATE)
    private GremlinEntityInformation information;

    private String generateIsEqual(@NonNull Criteria criteria) {
        String subject = criteria.getSubject();

        if (subject.equals(this.information.getIdField().getName())) {
            subject = PROPERTY_ID; // If subject is @Id/id field, use id property in database.
        }

        final String content = AbstractGremlinScriptLiteral.generateHas(subject, criteria.getSubValues().get(0));

        return String.format(GREMLIN_PRIMITIVE_WHERE, content);
    }

    private String generateUnaryScript(@NonNull Criteria criteria, CriteriaType type) {
        Assert.isTrue(Criteria.isUnaryOperation(type), "should be unary type of CriteriaType");

        final String subject = criteria.getSubject();
        final long milliSeconds = GremlinUtils.timeToMilliSeconds(criteria.getSubValues().get(0));

        final String values = String.format(GREMLIN_PRIMITIVE_VALUES, subject);
        final String query = String.format(CriteriaType.criteriaTypeToGremlin(type), milliSeconds);
        final String content = String.join(GREMLIN_PRIMITIVE_INVOKE, values, query);

        return String.format(GREMLIN_PRIMITIVE_WHERE, content);
    }

    private String generateBinaryScript(@NonNull String left, @NonNull String right, CriteriaType type) {
        Assert.isTrue(Criteria.isBinaryOperation(type), "should be binary type of CriteriaType");

        final String operation = CriteriaType.criteriaTypeToGremlin(type);
        final String content = String.join(GREMLIN_PRIMITIVE_INVOKE, left, operation, right);

        return String.format(GREMLIN_PRIMITIVE_WHERE, content);
    }

    private String generateScriptTraversal(@NonNull Criteria criteria) {
        final CriteriaType type = criteria.getType();

        switch (type) {
            case IS_EQUAL:
                return this.generateIsEqual(criteria);
            case AND:
            case OR:
                final String left = this.generateScriptTraversal(criteria.getSubCriteria().get(0));
                final String right = this.generateScriptTraversal(criteria.getSubCriteria().get(1));

                return this.generateBinaryScript(left, right, type);
            case AFTER:
            case BEFORE:
                return this.generateUnaryScript(criteria, type);
            default:
                throw new UnsupportedOperationException("unsupported Criteria type");
        }
    }

    private List<String> generateScript(@NonNull GremlinQuery query) {
        final Criteria criteria = query.getCriteria();
        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);

        if (this.information.isEntityVertex()) {
            scriptList.add(GREMLIN_PRIMITIVE_VERTEX_ALL);
        } else if (this.information.isEntityEdge()) {
            scriptList.add(GREMLIN_PRIMITIVE_EDGE_ALL);
        } else {
            throw new UnsupportedOperationException("Cannot generate script from graph entity");
        }

        scriptList.add(String.format(GREMLIN_PRIMITIVE_HAS_STRING, PROPERTY_LABEL, this.information.getEntityLabel()));
        scriptList.add(this.generateScriptTraversal(criteria));

        return scriptList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<String> generate(@NonNull GremlinQuery query, @NonNull Class<T> domainClass) {
        final List<String> scriptList = new ArrayList<>();

        this.setInformation(new GremlinEntityInformation(domainClass));

        scriptList.addAll(this.generateScript(query));

        return Collections.singletonList(String.join(GREMLIN_PRIMITIVE_INVOKE, scriptList));
    }
}

