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

@NoArgsConstructor
public abstract class AbstractGremlinResultReader {

    /**
     * properties's organization is a little complicated.
     *
     * properties is LinkedHashMap<K, V>
     * K is String
     * V is ArrayList<T>
     * T is LinkedHashMap<String, String>
     */
    private Object readProperty(@NonNull Object value) {
        Assert.isInstanceOf(ArrayList.class, value, "should be instance of ArrayList");

        @SuppressWarnings("unchecked") final ArrayList<LinkedHashMap<String, String>> mapList
                = (ArrayList<LinkedHashMap<String, String>>) value;

        Assert.isTrue(mapList.size() == 1, "should be only 1 element in ArrayList");

        return mapList.get(0).get(Constants.PROPERTY_VALUE);
    }

    protected void readResultProperties(@NonNull Map<String, Object> properties, @NonNull GremlinSource source) {
        Assert.isTrue(source.getProperties().isEmpty(), "should be empty GremlinSource");

        properties.forEach((key, value) -> source.setProperty(key, this.readProperty(value)));
    }
}
