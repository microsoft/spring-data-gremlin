/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import com.microsoft.spring.data.gremlin.exception.GremlinInvalidEntityIdFieldException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GremlinUtils {

    public static <T> T createInstance(@NonNull Class<T> type) {
        final T instance;

        try {
            instance = type.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("can not access type constructor", e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("failed to create instance of given type", e);
        }

        return instance;
    }

    public static <T> Field getIdField(@NonNull Class<T> domainClass) {
        final Field idField;
        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainClass, Id.class);

        if (fields.isEmpty()) {
            idField = ReflectionUtils.findField(domainClass, Constants.PROPERTY_ID);
        } else if (fields.size() == 1) {
            idField = fields.get(0);
        } else {
            throw new GremlinInvalidEntityIdFieldException("only one @Id field is allowed");
        }

        if (idField == null) {
            throw new GremlinInvalidEntityIdFieldException("no field named id in class");
        } else if (idField.getType() != String.class) {
            throw new GremlinInvalidEntityIdFieldException("the type of @Id/id field should be String");
        }

        return idField;
    }

    public static String getScriptByValue(@NonNull String name, @NonNull Object value) {
        if (value instanceof String) {
            return String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_STRING, name, value.toString());
        } else if (value instanceof Integer) {
            return String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_NUMBER, name, value);
        } else if (value instanceof Boolean) {
            return String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_BOOLEAN, name, value);
        } else {
            final String typeName = value.getClass().getName();
            throw new UnsupportedOperationException(String.format("unsupported type %s from gremlin", typeName));
        }
    }
}
