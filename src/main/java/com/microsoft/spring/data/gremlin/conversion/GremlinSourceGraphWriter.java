/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.annotation.EdgeSet;
import com.microsoft.spring.data.gremlin.annotation.VertexSet;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.List;

public class GremlinSourceGraphWriter extends BasicGremlinSourceWriter implements GremlinSourceWriter {

    public GremlinSourceGraphWriter(@NonNull Object domain) {
        super(domain);
    }

    private void writeGraphSet(final Field field, List<Object> objectList,
                               MappingGremlinConverter mappingConverter, GremlinSourceGraph sourceGraph) {
        Assert.notNull(field, "field should not be null");
        Assert.notNull(objectList, "GremlinSourcGraph should not be null");
        Assert.notNull(mappingConverter, "GremlinSourcGraph should not be null");
        Assert.notNull(sourceGraph, "GremlinSourcGraph should not be null");

        for (final Object object : objectList) {
            final GremlinEntityInformation information = new GremlinEntityInformation(object);
            final GremlinSource source = information.getGremlinSource();

            source.doGremlinSourceWrite(object, mappingConverter);
            sourceGraph.addGremlinSource(source);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(Object domain, MappingGremlinConverter converter, GremlinSource source) {
        if (domain == null || converter == null || source == null || source instanceof GremlinSourceGraph) {
            throw new IllegalArgumentException("Invalid argument of write method");
        }

        source.setId(super.getPersistentEntityId());

        final GremlinSourceGraph sourceGraph = (GremlinSourceGraph) source;
        final GremlinPersistentEntity<?> persistentEntity = converter.getPersistentEntity(domain.getClass());
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);

        for (final Field field : domain.getClass().getDeclaredFields()) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());
            Assert.notNull(property, "persistence property should not be null");
            final List<Object> objectList = (List<Object>) accessor.getProperty(property);

            if (field.getAnnotatedType().getType() == VertexSet.class
                || field.getAnnotatedType().getType() == EdgeSet.class) {
                this.writeGraphSet(field, objectList, converter, sourceGraph);
            }
        }
    }
}
