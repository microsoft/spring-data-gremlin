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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class QueryFindScriptGenerator implements QueryScriptGenerator {

    @Setter(AccessLevel.PRIVATE)
    private GremlinEntityInformation information;

    private List<String> generateIsEqual(@NonNull Criteria criteria) {
        String subject = criteria.getSubject();

        if (subject.equals(this.information.getIdField().getName())) {
            subject = Constants.PROPERTY_ID; // If subject is @Id/id field, use id property in database.
        }

        final List<String> scriptList = new ArrayList<>();
        final String label = this.information.getEntityLabel();

        scriptList.add(AbstractGremlinScriptLiteral.generateHas(Constants.PROPERTY_LABEL, label));
        scriptList.add(AbstractGremlinScriptLiteral.generateHas(subject, criteria.getSubValues().get(0)));

        return scriptList;
    }

    private List<String> generateScriptTraversal(@NonNull Criteria criteria) {
        final CriteriaType type = criteria.getType();
        final List<String> scriptList = new ArrayList<>();

        switch (type) {
            case IS_EQUAL:
                return this.generateIsEqual(criteria);
            case AND:
                Assert.isTrue(criteria.getSubCriteria().size() == 2, "And should contains 2 subCriteria");

                scriptList.addAll(this.generateScriptTraversal(criteria.getSubCriteria().get(0)));

                scriptList.add(Constants.GREMLIN_PRIMITIVE_AND);

                scriptList.addAll(this.generateScriptTraversal(criteria.getSubCriteria().get(1)));

                return scriptList;
            default:
                // TODO(panli): More keyword will be add in future
                throw new UnsupportedOperationException("unsupported Criteria type");
        }
    }

    private List<String> generateScript(@NonNull GremlinQuery query) {
        final Criteria criteria = query.getCriteria();
        final List<String> scriptList = new ArrayList<>();

        if (this.information.isEntityVertex()) {
            scriptList.add(Constants.GREMLIN_PRIMITIVE_VERTEX_ALL);
        } else if (this.information.isEntityEdge()) {
            scriptList.add(Constants.GREMLIN_PRIMITIVE_EDGE_ALL);
        } else {
            throw new UnsupportedOperationException("Cannot generate script from graph entity");
        }

        scriptList.addAll(this.generateScriptTraversal(criteria));

        return scriptList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<String> generate(@NonNull GremlinQuery query, @NonNull Class<T> domainClass) {
        final List<String> scriptList = new ArrayList<>();

        this.setInformation(new GremlinEntityInformation(domainClass));

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.addAll(this.generateScript(query));

        return Collections.singletonList(String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList));
    }
}

