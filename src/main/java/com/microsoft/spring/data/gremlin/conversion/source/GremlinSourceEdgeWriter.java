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
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

@NoArgsConstructor
public class GremlinSourceEdgeWriter implements GremlinSourceWriter {

    private String getIdValue(@NonNull Object object, @NonNull MappingGremlinConverter converter) {
        if (object instanceof String) {
            return object.toString();
        } else if (object.getClass().isPrimitive()) {
            throw new GremlinUnexpectedEntityTypeException("only String type of primitive is allowed");
        } else {
            return converter.getIdFieldValue(object).toString();
        }
    }

    @Override
    public void write(@NonNull Object domain, @NonNull MappingGremlinConverter converter,
                      @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        source.setId(converter.getIdFieldValue(domain));

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;
        final GremlinPersistentEntity<?> persistentEntity = converter.getPersistentEntity(domain.getClass());
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);

        for (final Field field : FieldUtils.getAllFields(domain.getClass())) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());
            Assert.notNull(property, "persistence property should not be null");

            final Object object = accessor.getProperty(property);

            if (field.getName().equals(Constants.PROPERTY_ID) || field.getAnnotation(Id.class) != null) {
                continue;
            } else if (field.getAnnotation(EdgeFrom.class) != null) {
                sourceEdge.setVertexIdFrom(this.getIdValue(object, converter));
            } else if (field.getAnnotation(EdgeTo.class) != null) {
                sourceEdge.setVertexIdTo(this.getIdValue(object, converter));
            } else if (!field.getName().equals(Constants.PROPERTY_ID)) {
                source.setProperty(field.getName(), accessor.getProperty(property));
            }
        }
    }
}

