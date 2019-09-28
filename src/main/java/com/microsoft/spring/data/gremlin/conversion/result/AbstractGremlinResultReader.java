/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.result;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.microsoft.spring.data.gremlin.common.Constants.*;
import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_VALUE_WITH_AT;

@NoArgsConstructor
// TODO: seems only for Vertex.
public abstract class AbstractGremlinResultReader {

    /**
     * Vertex's properties returned from gremlin-driver has a complicated data structure
     * This function helps to renovate it to a simple Map
     * @return Map of list properties
     */
    protected Map<String, Object> getProperties (@NonNull Map<String, Object> map) {
        while ((map instanceof LinkedHashMap) && map.containsKey(PROPERTY_VALUE_WITH_AT)) {
            final Object value = map.get(PROPERTY_VALUE_WITH_AT);
            if (value instanceof ArrayList && ((ArrayList) value).size() > 0) {
                map = (Map<String, Object>) ((ArrayList) value).get(0);
            } else {
                map = (Map<String, Object>) value;
            }
        }

        return map;
    }

    protected Object getPropertyValue (@NonNull Map<String, Object> map, @NonNull String propertyKey) {
        Object value = map.get(propertyKey);

        while ((value instanceof LinkedHashMap) && ((LinkedHashMap) value).containsKey(PROPERTY_VALUE_WITH_AT)) {
            value = ((LinkedHashMap) value).get(PROPERTY_VALUE_WITH_AT);
        }

        return value;
    }

    /**
     * properties's organization is a little complicated.
     * <p>
     * properties is LinkedHashMap<K, V>
     * K is String
     * V is ArrayList<T>
     * T is LinkedHashMap<String, String>
     */
    private Object readProperty(@NonNull Object value) {
        Assert.isInstanceOf(ArrayList.class, value, "should be instance of ArrayList");

        @SuppressWarnings("unchecked") final ArrayList<LinkedHashMap<String, Object>> mapList
                = (ArrayList<LinkedHashMap<String, Object>>) value;

        Assert.isTrue(mapList.size() == 1, "should be only 1 element in ArrayList");

        final Map<String, Object> renovatedMap = getProperties(mapList.get(0));

        return renovatedMap.get(Constants.PROPERTY_VALUE);
    }

    protected void readResultProperties(@NonNull Map<String, Object> properties, @NonNull GremlinSource source) {
        source.getProperties().clear();
        properties.forEach((key, value) -> source.setProperty(key, this.readProperty(value)));
    }

    protected void readIdentifier(@NonNull Map<String, Object> dataMap, @NonNull GremlinSource source) {
        Assert.isTrue(
                source.getLabel().equals(dataMap.get(PROPERTY_LABEL)),
                "mapping an invalid object data: " + dataMap.get(PROPERTY_LABEL));

        if (source.getId() == null && dataMap.containsKey(PROPERTY_ID)) {
            source.setId(dataMap.get(PROPERTY_ID));
        }
    }
}
