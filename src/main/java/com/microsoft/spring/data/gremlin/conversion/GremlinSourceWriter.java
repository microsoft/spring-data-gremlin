/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

/**
 * Provider Entity type dependent write method.
 */
public interface GremlinSourceWriter {
    /**
     * Write the domain class information to GremlinSource
     */
    void writeEnityToGremlinSource(Class domain, GremlinSource source);
}
