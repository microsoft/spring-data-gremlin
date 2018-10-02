/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import com.microsoft.spring.data.gremlin.annotation.EdgeSet;
import com.microsoft.spring.data.gremlin.annotation.VertexSet;
import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GremlinSourceGraphReader extends AbstractGremlinSourceReader implements GremlinSourceReader {

    @Override
    public <T> T read(@NonNull Class<T> type, @NonNull MappingGremlinConverter converter,
            @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("Should be instance of GremlinSourceGraph");
        }
        final GremlinSourceGraph graphSource = (GremlinSourceGraph) source;
        final T domain = GremlinUtils.createInstance(type);
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);
        final GremlinPersistentEntity persistentEntity = converter.getPersistentEntity(type);
        final List<GremlinSource> vertexSources = graphSource.getVertexSet();
        final List<GremlinSource> edgeSources = graphSource.getEdgeSet();

        for (final Field field : FieldUtils.getAllFields(type)) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());
            Assert.notNull(property, "persistence property should not be null");

            if ((field.getName().equals(Constants.PROPERTY_ID) || field.getAnnotation(Id.class) != null) 
                    && source.getId() != null) {
                accessor.setProperty(property, super.getGremlinSourceId(graphSource));
            } else if (field.isAnnotationPresent(VertexSet.class) && vertexSources != null) {
                final Class<?> vertexClass = GremlinUtils.determineGraphVertexType(type);
                final List<Object> vertexObjects = buildDomainObjects(vertexSources, converter, vertexClass);
                accessor.setProperty(property, vertexObjects);
            } else if (field.isAnnotationPresent(EdgeSet.class) && edgeSources != null) {
                final Class<?> edgeClass = GremlinUtils.determineGraphEdgeType(type);
                final List<Object> edgeObjects = buildDomainObjects(edgeSources, converter, edgeClass);
                accessor.setProperty(property, edgeObjects);
            }
        }
        return domain;
    }

    private List<Object> buildDomainObjects(List<GremlinSource> sources, MappingGremlinConverter converter, 
            Class<?> domainClass) {
        final List<Object> domainObjects = new ArrayList<>();
        for (final GremlinSource source : sources) {
            source.setIdField(new GremlinEntityInformation<>(domainClass).getIdField());
            final Object domainObject = source.doGremlinSourceRead(domainClass, converter);
            domainObjects.add(domainObject);
        }
        return domainObjects;
    }

}

