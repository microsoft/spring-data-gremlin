/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentProperty;
import com.microsoft.spring.data.gremlin.query.criteria.Criteria;
import com.microsoft.spring.data.gremlin.query.criteria.CriteriaType;
import com.microsoft.spring.data.gremlin.query.paramerter.GremlinParameterAccessor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.lang.NonNull;

import java.util.*;

public class GremlinQueryCreator extends AbstractQueryCreator<GremlinQuery, Criteria> {

    private final MappingContext<?, GremlinPersistentProperty> mappingContext;
    private static final Map<Part.Type, CriteriaType> criteriaMap;

    static {
        final Map<Part.Type, CriteriaType> map = new HashMap<>();

        map.put(Part.Type.SIMPLE_PROPERTY, CriteriaType.IS_EQUAL);

        criteriaMap = Collections.unmodifiableMap(map);
    }

    public GremlinQueryCreator(@NonNull PartTree partTree, @NonNull GremlinParameterAccessor accessor,
                               @NonNull MappingContext<?, GremlinPersistentProperty> mappingContext) {
        super(partTree, accessor);

        this.mappingContext = mappingContext;
    }

    @Override
    protected Criteria create(@NonNull Part part, @NonNull Iterator<Object> parameters) {
        final Part.Type type = part.getType();
        final String subject = this.mappingContext.getPersistentPropertyPath(part.getProperty()).toDotPath();
        final List<Object> values = new ArrayList<>();

        if (!criteriaMap.containsKey(type)) {
            throw new UnsupportedOperationException("Unsupported keyword: " + type.toString());
        }

        parameters.forEachRemaining(values::add);

        return Criteria.getInstance(subject, criteriaMap.get(type), values);
    }

    @Override
    protected Criteria and(@NonNull Part part, @NonNull Criteria base, @NonNull Iterator<Object> iterator) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    protected Criteria or(@NonNull Criteria base, @NonNull Criteria criteria) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    protected GremlinQuery complete(@NonNull Criteria criteria, @NonNull Sort sort) {
        return new GremlinQuery(criteria);
    }
}
