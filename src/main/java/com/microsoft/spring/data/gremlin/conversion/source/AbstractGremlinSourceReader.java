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

public class AbstractGremlinSourceReader {

    protected <T extends Object> List<String> findDomainFieldsNames(@NonNull Class<T> type,
                                                     @NonNull GremlinPersistentEntity persistentEntity) {
        final List<String> fieldNames = new ArrayList<>();

        Arrays.asList(type.getDeclaredFields()).forEach(field -> {
            final Class<?> fieldType = persistentEntity.getPersistentProperty(field.getName()).getField().getType();

            switch (fieldType.getName()) {
                case "int":
                case "boolean":
                case "java.lang.String":
                case "java.lang.Integer":
                case "java.lang.Boolean":
                    fieldNames.add(field.getName());
                    break;
                case "java.util.Map":
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("unsupported type %s", fieldType.toString()));

            }
        });

        return fieldNames;
    }

    protected void readDomainMapField(@NonNull ConvertingPropertyAccessor accessor, @NonNull PersistentProperty property,
                                    @NonNull GremlinSource source, @NonNull List<String> fieldNames) {
        final Map<String, Object> mapProperty = new HashMap<>();
        Assert.isTrue(property.getField().getType() == Map.class, "should be Map field.");

        source.getProperties().forEach((key, value) -> {
            if (!fieldNames.contains(key)) {
                mapProperty.put(key, value);
            }
        });

        accessor.setProperty(property, mapProperty);
    }
}

