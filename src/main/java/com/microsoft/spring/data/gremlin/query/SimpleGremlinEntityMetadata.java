/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

public class SimpleGremlinEntityMetadata<T> implements GremlinEntityMetadata<T> {

    private final Class<T> type;

    public SimpleGremlinEntityMetadata(Class<T> type) {
        this.type = type;
    }

    public Class<T> getJavaType() {
        return this.type;
    }
}
