/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;

import java.util.List;

/**
 * Provider interface to generate different query to gremlin server.
 * The scripts return queries in steps, organized by List.
 */
public interface GremlinScriptLiteral {
    /**
     * Generate the insert query from source (Vertex, Edge or Graph).
     */
    List<String> generateInsertScript(GremlinSource source);

    /**
     * Generate the deleteAll query from source (Vertex, Edge or Graph).
     */
    List<String> generateDeleteAllScript(GremlinSource source);

    /**
     * Generate the deleteAll By Domain Class query from source (Vertex, Edge or Graph).
     */
    List<String> generateDeleteAllByClassScript(GremlinSource source);

    /**
     * Generate the findById query from source (Vertex, Edge).
     */
    List<String> generateFindByIdScript(GremlinSource source);

    /**
     * Generate the update query from source (Vertex, Edge or Graph).
     */
    List<String> generateUpdateScript(GremlinSource source);

    /**
     * Generate the findAll query from source (Vertex, Edge or Graph).
     */
    List<String> generateFindAllScript(GremlinSource source);

    /**
     * Generate the DeleteById query from source (Vertex, Edge or Graph).
     */
    List<String> generateDeleteByIdScript(GremlinSource source);

    /**
     * Generate the Count query from Source (Vertex, Edge)
     */
    List<String> generateCountScript(GremlinSource source);
}
