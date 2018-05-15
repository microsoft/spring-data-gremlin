/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.exception.GremlinSourcePropertiesConflictException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicGremlinScriptLiteral {

    private List<String> generateMapProperties(@NonNull String key, @NonNull Map<String, Object> valueMap,
                                               @NonNull Map<String, Object> properties) {
        final List<String> scriptList = new ArrayList<>();

        Assert.isTrue(properties.containsKey(key), String.format("should contains name %s", key));
        Assert.isInstanceOf(Map.class, properties.get(key), "property value should be instance of map.");

        for (final Map.Entry<String, Object> entry : valueMap.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();

            if (properties.containsKey(name)) {
                throw new GremlinSourcePropertiesConflictException(String.format("conflict property name %s", name));
            } else if (value instanceof String) {
                scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_STRING, name, value.toString()));
            } else if (value instanceof Integer) {
                scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_NUMBER, name, value));
            } else if (value instanceof Boolean) {
                scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_BOOLEAN, name, value));
            } else {
                final String typeName = value.getClass().getName();
                throw new NotImplementedException(String.format("unsupported type %s from gremlin", typeName));
            }
        }

        return scriptList;
    }

    protected void generateProperties(@NonNull List<String> scriptList, @NonNull final Map<String, Object> properties) {
        boolean hasMapProperty = false;

        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();

            if (value instanceof String) {
                scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_STRING, name, value.toString()));
            } else if (value instanceof Integer) {
                scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_NUMBER, name, value));
            } else if (value instanceof Boolean) {
                scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_BOOLEAN, name, value));
            } else if (value instanceof Map && hasMapProperty) {
                throw new GremlinUnexpectedEntityTypeException("only one Map field is allowed.");
            } else if (value instanceof Map) {
                hasMapProperty = true;
                @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) value;
                scriptList.addAll(this.generateMapProperties(name, map, properties));
            } else {
                final String typeName = value.getClass().getName();
                throw new NotImplementedException(String.format("unsupported type %s from gremlin", typeName));
            }
        }
    }
}
