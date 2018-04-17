/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GremlinSourceSimpleFactory {

    public static GremlinSource createGremlinSource(@NonNull Field idField, @NonNull String label,
                                                    GremlinEntityType type) {
        switch (type) {
            case VERTEX:
                return new GremlinSourceVertex(idField, label);
            case EDGE:
                return new GremlinSourceEdge(idField, label);
            case GRAPH:
                return new GremlinSourceGraph(idField, label);
            default:
                throw new GremlinUnexpectedEntityTypeException("Unexpected gremlin entity type");
        }
    }
}

