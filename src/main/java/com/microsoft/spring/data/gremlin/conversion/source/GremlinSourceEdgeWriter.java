/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import lombok.NoArgsConstructor;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

@NoArgsConstructor
public class GremlinSourceEdgeWriter implements GremlinSourceWriter {

    @Override
    public void write(@NonNull Object domain, @NonNull MappingGremlinConverter converter,
                      @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        source.setId(converter.getFieldValue(domain, source.getIdField().getName()).toString());

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
                sourceEdge.setVertexIdFrom(converter.getIdFieldValue(object).toString());
                continue;
            } else if (field.getAnnotation(EdgeTo.class) != null) {
                sourceEdge.setVertexIdTo(converter.getIdFieldValue(object).toString());
                continue;
            }

            source.setProperty(field.getName(), accessor.getProperty(property));
        }
    }
}

