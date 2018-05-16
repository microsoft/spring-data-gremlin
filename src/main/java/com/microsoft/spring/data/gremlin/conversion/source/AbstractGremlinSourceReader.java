/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.*;

public abstract class AbstractGremlinSourceReader {

    private static final List<String> SUPPORTED_TYPE_NAME = Arrays.asList("int", "boolean",
            "java.lang.String", "java.lang.Integer", "java.lang.Boolean", "java.util.Map"
    );

    protected <T extends Object> List<String> findDomainFieldsNames(@NonNull Class<T> type,
                                                                    @NonNull GremlinPersistentEntity persistentEntity) {
        final List<String> fieldNames = new ArrayList<>();

        Arrays.asList(type.getDeclaredFields()).forEach(field -> {
            final Class<?> fieldType = persistentEntity.getPersistentProperty(field.getName()).getField().getType();

            if (SUPPORTED_TYPE_NAME.contains(fieldType.getName())) {
                fieldNames.add(field.getName());
            } else {
                throw new UnsupportedOperationException(String.format("unsupported type %s", fieldType.toString()));
            }
        });

        return fieldNames;
    }

    protected void readDomainMapField(@NonNull ConvertingPropertyAccessor accessor,
                                      @NonNull PersistentProperty property,
                                      @NonNull GremlinSource source, @NonNull List<String> fieldNames) {
        Assert.isTrue(property.getTypeInformation().getType() == Map.class, "should be Map type.");

        final Map<String, Object> mapProperty = new HashMap<>();

        source.getProperties().forEach((key, value) -> {
            if (!fieldNames.contains(key) || property.getName().equals(key)) {
                mapProperty.put(key, value);
            }
        });

        accessor.setProperty(property, mapProperty);
    }
}

