/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.support;

import com.microsoft.spring.data.gremlin.annotation.Edge;
import com.microsoft.spring.data.gremlin.annotation.Graph;
import com.microsoft.spring.data.gremlin.annotation.Vertex;
import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import lombok.Getter;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static com.microsoft.spring.data.gremlin.common.GremlinEntityType.*;

public class GremlinEntityInformation<T, ID> extends AbstractEntityInformation<T, ID> {

    @Getter
    private final Field idField;

    @Getter
    private final GremlinSource<T> gremlinSource;

    public GremlinEntityInformation(@NonNull Class<T> domainClass) {
        super(domainClass);

        this.idField = this.getIdField(domainClass);
        this.gremlinSource = this.createGremlinSource(domainClass, this.idField);
    }

    private GremlinSource<T> createGremlinSource(@NonNull Class<T> domainClass, @NonNull Field idField) {
        final String label;
        final String domainClassName = domainClass.getSimpleName();
        final GremlinEntityType type;
        final Vertex vertex = domainClass.getAnnotation(Vertex.class);
        final Edge edge = domainClass.getAnnotation(Edge.class);
        final Graph graph = domainClass.getAnnotation(Graph.class);

        if (vertex != null && edge == null && graph == null) {
            type = VERTEX;
            label = vertex.label().isEmpty() ? domainClassName : vertex.label();
        } else if (edge != null && vertex == null && graph == null) {
            type = EDGE;
            label = edge.label().isEmpty() ? domainClassName : edge.label();
        } else if (graph != null && vertex == null && edge == null) {
            type = GRAPH;
            label = "";
        } else {
            throw new GremlinUnexpectedEntityTypeException("Unexpected gremlin entity type");
        }

        @SuppressWarnings("unchecked") final GremlinSource<T> source = (GremlinSource<T>) type.createGremlinSource();

        source.setLabel(label);
        source.setIdField(idField);
        source.setDomainClass(domainClass);

        return source;
    }

    @Override
    @Nullable
    public ID getId(T entity) {
        @SuppressWarnings("unchecked") final ID id = (ID) ReflectionUtils.getField(this.idField, entity);

        return id;
    }

    @Override
    public Class<ID> getIdType() {
        @SuppressWarnings("unchecked") final Class<ID> idClass = (Class<ID>) this.idField.getType();

        return idClass;
    }

    @NonNull
    private Field getIdField(@NonNull Class<T> domainClass) {
        final Field idField = GremlinUtils.getIdField(domainClass);

        ReflectionUtils.makeAccessible(idField);

        return idField;
    }
}

