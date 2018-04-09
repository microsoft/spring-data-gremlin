/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Map;

@NoArgsConstructor
public class BasicGremlinResultReader {

    void readResultProperties(@NonNull Map<String, Object> properties, @NonNull GremlinSource source) {

        Assert.isTrue(source.getProperties().isEmpty(), "should be empty GremlinSource");

        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();

            source.setProperty(name, value);
        }
    }
}
