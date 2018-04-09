/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.exception.UnexpectedGremlinSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

public class GremlinSourceEdgeWriter extends BasicGremlinSourceWriter implements GremlinSourceWriter {

    public GremlinSourceEdgeWriter(@NonNull Field idField, @NonNull String label) {
        super(idField, label);
    }

    @Override
    public void write(Object domain, MappingGremlinConverter converter, GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new UnexpectedGremlinSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        source.setId(super.getEntityIdValue(domain, converter));
        source.setLabel(super.getEntityLabel());

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;
        final GremlinPersistentEntity<?> persistentEntity = converter.getPersistentEntity(domain.getClass());
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);

        for (final Field field : domain.getClass().getDeclaredFields()) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());
            Assert.notNull(property, "persistence property should not be null");

            final Object object = accessor.getProperty(property);

            if (field.getName().equals(Constants.PROPERTY_ID)) {
                continue;
            } else if (field.getAnnotation(EdgeFrom.class) != null) {
                sourceEdge.setVertexIdFrom(super.getEntityIdValue(object, converter));
                continue;
            } else if (field.getAnnotation(EdgeTo.class) != null) {
                sourceEdge.setVertexIdTo(super.getEntityIdValue(object, converter));
                continue;
            }

            source.setProperty(field.getName(), accessor.getProperty(property));
        }
    }
}

