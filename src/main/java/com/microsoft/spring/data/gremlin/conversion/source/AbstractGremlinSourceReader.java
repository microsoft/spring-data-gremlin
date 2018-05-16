/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import org.apache.tinkerpop.shaded.jackson.databind.JavaType;
import org.apache.tinkerpop.shaded.jackson.databind.MapperFeature;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.shaded.jackson.databind.type.TypeFactory;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.lang.NonNull;

import java.io.IOException;

public abstract class AbstractGremlinSourceReader {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, false);
    }

    protected Object readProperty(@NonNull PersistentProperty property, @NonNull Object value) {
        final Class<?> type = property.getTypeInformation().getType();
        final JavaType javaType = TypeFactory.defaultInstance().constructType(property.getType());

        if (type == int.class || type == Integer.class
                || type == Boolean.class || type == boolean.class
                || type == String.class) {
            return value;
        } else {
            final Object object;

            try {
                object = mapper.readValue(value.toString(), javaType);
            } catch (IOException e) {
                throw new GremlinUnexpectedEntityTypeException("Failed to read String to Object", e);
            }

            return object;
        }
    }
}

