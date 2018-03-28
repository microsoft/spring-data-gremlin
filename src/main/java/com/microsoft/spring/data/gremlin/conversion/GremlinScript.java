/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

/**
 * Provider interface to generate query to gremlin server.
 * For now, gremlin client can accept String literal and GraphTraversal.
 */
public interface GremlinScript<T> {
    /**
     * Generate the final script before submit query to gremlin server.
     *
     * @return gremlin client accepted String or GraphTraversal.
     */
    T generateScript(GremlinSource gremlinSource);
}
