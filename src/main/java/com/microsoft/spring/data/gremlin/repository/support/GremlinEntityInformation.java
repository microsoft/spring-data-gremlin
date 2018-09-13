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
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceSimpleFactory;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import lombok.Getter;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class GremlinEntityInformation<T, ID> extends AbstractEntityInformation<T, ID> {

    @Getter
    private Field idField;

    @Getter
    private String entityLabel;

    @Getter
    private GremlinEntityType entityType;

    @Getter
    private GremlinSource gremlinSource;

    public GremlinEntityInformation(@NonNull Class<T> domainClass) {
        super(domainClass);

        this.idField = this.getIdField(domainClass);
        ReflectionUtils.makeAccessible(this.idField);

        this.entityType = this.getGremlinEntityType(domainClass); // The other fields getter may depend on type
        this.entityLabel = this.getEntityLabel(domainClass);
        this.gremlinSource = this.createGremlinSource();
    }

    public boolean isEntityEdge() {
        return this.getEntityType() == GremlinEntityType.EDGE;
    }

    public boolean isEntityVertex() {
        return this.getEntityType() == GremlinEntityType.VERTEX;
    }

    public boolean isEntityGraph() {
        return this.getEntityType() == GremlinEntityType.GRAPH;
    }

    @Override
    @Nullable
    public ID getId(T entity) {
        @SuppressWarnings("unchecked") final ID id = (ID) ReflectionUtils.getField(this.getIdField(), entity);

        return id;
    }

    @Override
    public Class<ID> getIdType() {
        @SuppressWarnings("unchecked") final Class<ID> idClass = (Class<ID>) this.idField.getType();

        return idClass;
    }

    @NonNull
    private Field getIdField(@NonNull Class<T> domainClass) {
        return GremlinUtils.getIdField(domainClass);
    }

    private GremlinEntityType getGremlinEntityType(@NonNull Class<?> domainClass) {
        final Vertex vertexAnnotation = domainClass.getAnnotation(Vertex.class);

        if (vertexAnnotation != null) {
            return GremlinEntityType.VERTEX;
        }

        final Edge edgeAnnotation = domainClass.getAnnotation(Edge.class);

        if (edgeAnnotation != null) {
            return GremlinEntityType.EDGE;
        }

        final Graph graphAnnotation = domainClass.getAnnotation(Graph.class);

        if (graphAnnotation != null) {
            return GremlinEntityType.GRAPH;
        }

        throw new GremlinUnexpectedEntityTypeException("cannot not to identify gremlin entity type");
    }

    private String getEntityLabel(@NonNull Class<?> domainClass) {
        final String label;

        switch (this.entityType) {
            case VERTEX:
                final Vertex vertexAnnotation = domainClass.getAnnotation(Vertex.class);

                if (vertexAnnotation == null || vertexAnnotation.label().isEmpty()) {
                    label = domainClass.getSimpleName();
                } else {
                    label = vertexAnnotation.label();
                }
                break;
            case EDGE:
                final Edge edgeAnnotation = domainClass.getAnnotation(Edge.class);

                if (edgeAnnotation == null || edgeAnnotation.label().isEmpty()) {
                    label = domainClass.getSimpleName();
                } else {
                    label = edgeAnnotation.label();
                }
                break;
            case GRAPH:
                label = null;
                break;
            case UNKNOWN:
                // fallthrough
            default:
                throw new GremlinUnexpectedEntityTypeException("Unexpected gremlin entity type");
        }

        return label;
    }

    private GremlinSource createGremlinSource() {
        return GremlinSourceSimpleFactory.createGremlinSource(getIdField(), getEntityLabel(), getEntityType());
    }
}

