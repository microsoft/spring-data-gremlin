/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

/**
 * Provider interface to generate different instance of GremlinScript.
 * Like GremlinScriptLiteral and GremlinScriptTraversal.
 */
public interface GremlinScriptFactory {
    /**
     * Generate the instance of GremlinScript implementor.
     *
     * @return GremlinScript
     */
    GremlinScript createGremlinScript();
}
