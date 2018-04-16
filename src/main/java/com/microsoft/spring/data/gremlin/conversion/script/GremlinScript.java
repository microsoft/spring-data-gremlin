/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;

/**
 * Provider interface to generate query to gremlin server.
 * For now, gremlin client can accept String literal and GraphTraversal.
 */
public interface GremlinScript<T> {
    /**
     * Generate the final insert script query to gremlin server.
     *
     * @return gremlin client accepted query.
     */
    T generateInsertScript(GremlinSource source);


    /**
     * Generate the final delete script query to gremlin server.
     *
     * @return gremlin client accepted query.
     */
    T generateDeleteScript(GremlinSource source);


    /**
     * Generate the final findById script query to gremlin server.
     *
     * @return gremlin client accepted query.
     */
    T generateFindByIdScript(GremlinSource source);
}
