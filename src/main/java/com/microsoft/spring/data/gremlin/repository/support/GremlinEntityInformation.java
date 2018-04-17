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
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScript;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptEdgeLiteral;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptGraphLiteral;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptVertexLiteral;
import com.microsoft.spring.data.gremlin.conversion.source.*;
import com.microsoft.spring.data.gremlin.exception.UnexpectedGremlinEntityTypeException;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class GremlinEntityInformation<T, ID> extends AbstractEntityInformation<T, ID> {

    private Field id;
    private String entityLabel;
    private GremlinEntityType entityType;
    private GremlinSource gremlinSource;

    public GremlinEntityInformation(@NonNull Class<T> domainClass) {
        super(domainClass);

        this.id = this.getIdField(domainClass);
        this.entityType = this.getGremlinEntityType(domainClass); // The other fields getter may depend on type
        this.entityLabel = this.getEntityLabel(domainClass);
        this.gremlinSource = this.getGremlinSource(domainClass);
    }

    public GremlinEntityType getEntityType() {
        return this.entityType;
    }

    public GremlinSource getGremlinSource() {
        return this.gremlinSource;
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

    @NonNull
    public String getEntityLabel() {
        return this.entityLabel;
    }

    @NonNull
    public Field getIdField() {
        return this.id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T entity) {
        return (ID) ReflectionUtils.getField(this.id, entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ID> getIdType() {
        return (Class<ID>) this.id.getType();
    }

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

        throw new UnexpectedGremlinEntityTypeException("cannot not to identify gremlin entity type");
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
                throw new UnexpectedGremlinEntityTypeException("Unexpected gremlin entity type");
        }

        return label;
    }

    private GremlinSource getGremlinSource(@NonNull Class<T> domainClass) {
        final GremlinSource source;
        final GremlinScript script;
        final GremlinSourceWriter writer;

        switch (this.entityType) {
            case VERTEX:
                source = new GremlinSourceVertex();
                script = new GremlinScriptVertexLiteral();
                writer = new GremlinSourceVertexWriter(this.getIdField(), this.getEntityLabel());
                break;
            case EDGE:
                source = new GremlinSourceEdge();
                script = new GremlinScriptEdgeLiteral();
                writer = new GremlinSourceEdgeWriter(this.getIdField(), this.getEntityLabel());
                break;
            case GRAPH:
                source = new GremlinSourceGraph();
                script = new GremlinScriptGraphLiteral();
                writer = new GremlinSourceGraphWriter(this.getIdField(), this.getEntityLabel());
                break;
            case UNKNOWN:
                // fallthrough
            default:
                throw new IllegalArgumentException("Unexpected gremlin entity type");
        }

        source.setGremlinScriptStrategy(script);
        source.setGremlinSourceWriter(writer);

        return source;
    }
}

