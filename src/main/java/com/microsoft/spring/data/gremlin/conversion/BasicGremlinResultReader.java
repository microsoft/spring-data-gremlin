/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.common.Constants;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
public class BasicGremlinResultReader {

    /**
     * properties's organization is a little complicated.
     *
     * properties is LinkedHashMap<K, V>
     *     K is String
     *     V is ArrayList<T>
     *         T is LinkedHashMap<String, String>
     */
    @SuppressWarnings("unchecked")
    protected void readResultProperties(@NonNull Map<String, Object> properties, @NonNull GremlinSource source) {
        Assert.isTrue(source.getProperties().isEmpty(), "should be empty GremlinSource");

        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();

            Assert.isInstanceOf(ArrayList.class, value, "should be instance of ArrayList");
            final ArrayList<LinkedHashMap<String, String>> mapList = (ArrayList<LinkedHashMap<String, String>>) value;

            Assert.isTrue(mapList.size() == 1, "should be only 1 element in ArrayList");
            final LinkedHashMap<String, String> map = mapList.get(0);

            source.setProperty(name, map.get(Constants.PROPERTY_VALUE));
        }
    }
}
