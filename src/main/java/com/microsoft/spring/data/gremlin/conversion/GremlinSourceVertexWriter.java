/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

public class GremlinSourceVertexWriter extends BasicGremlinSourceWriter implements GremlinSourceWriter {

    public GremlinSourceVertexWriter(@NonNull Class<?> domainClass) {
        super(domainClass);
    }

    @Override
    public void write(Object domain, MappingGremlinConverter converter, GremlinSource source) {
        if (domain == null || converter == null || source == null || source instanceof GremlinSourceVertex) {
            throw new IllegalArgumentException("Invalid argument of write method");
        }

        super.setGremlinSourceReserved(source);

        final GremlinPersistentEntity<?> persistentEntity = converter.getPersistentEntity(domain.getClass());
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);

        for (final Field field : domain.getClass().getDeclaredFields()) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());
            Assert.notNull(property, "persistence property should not be null");

            if (field.getName().equals(Constants.PROPERTY_ID)) {
                continue;
            }

            source.setProperty(field.getName(), accessor.getProperty(property));
        }
    }
}

