/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;

/**
 * Provider interface to generate different query to gremlin server.
 */
public interface GremlinScriptLiteral {
    /**
     * Generate the insert query from source (Vertex, Edge or Graph).
     */
    String generateInsertScript(GremlinSource source);


    /**
     * Generate the deleteAll query from source (Vertex, Edge or Graph).
     */
    String generateDeleteAllScript(GremlinSource source);


    /**
     * Generate the findById query from source (Vertex, Edge or Graph).
     */
    String generateFindByIdScript(GremlinSource source);

    /**
     * Generate the update query from source (Vertex, Edge or Graph).
     */
    String generateUpdateScript(GremlinSource source);
}
