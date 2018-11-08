/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.annotation.EdgeSet;
import com.microsoft.spring.data.gremlin.annotation.VertexSet;
import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_ID;

@NoArgsConstructor
public class GremlinSourceGraphReader extends AbstractGremlinSourceReader implements GremlinSourceReader {

    @Override
    public <T> T read(@NonNull Class<T> type, @NonNull MappingGremlinConverter converter,
                      @NonNull GremlinSource<T> source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("Should be instance of GremlinSourceGraph");
        }

        final GremlinSourceGraph<T> graphSource = (GremlinSourceGraph<T>) source;
        final T entity = GremlinUtils.createInstance(type);
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(entity);
        final GremlinPersistentEntity persistentEntity = converter.getPersistentEntity(type);

        for (final Field field : FieldUtils.getAllFields(type)) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());
            Assert.notNull(property, "persistence property should not be null");

            if ((field.getName().equals(PROPERTY_ID) || field.getAnnotation(Id.class) != null)) {
                accessor.setProperty(property, super.getGremlinSourceId(graphSource));
            } else if (field.isAnnotationPresent(VertexSet.class)) {
                accessor.setProperty(property, readEntitySet(graphSource.getVertexSet(), converter));
            } else if (field.isAnnotationPresent(EdgeSet.class)) {
                accessor.setProperty(property, readEntitySet(graphSource.getEdgeSet(), converter));
            }
        }

        return entity;
    }

    private List<Object> readEntitySet(List<GremlinSource> sources, MappingGremlinConverter converter) {
        Class<?> domainClass;
        final List<Object> domainObjects = new ArrayList<>();

        for (final GremlinSource source : sources) {
            try {
                domainClass = Class.forName((String) source.getProperties().get(Constants.GREMLIN_PROPERTY_CLASSNAME));
            } catch (ClassNotFoundException e) {
                throw new GremlinUnexpectedSourceTypeException("No Java class found for source property "
                        + Constants.GREMLIN_PROPERTY_CLASSNAME, e);
            }

            // TODO: seems unnecessary here.
            source.setIdField(new GremlinEntityInformation<>(domainClass).getIdField());
            domainObjects.add(source.doGremlinSourceRead(domainClass, converter));
        }

        return domainObjects;
    }
}
