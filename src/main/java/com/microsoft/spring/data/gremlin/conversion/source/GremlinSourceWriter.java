/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;

/**
 * Provider Entity type dependent write method.
 */
public interface GremlinSourceWriter {
    /**
     * Write the domain class information to GremlinSource
     */
    void write(Object domain, MappingGremlinConverter converter, GremlinSource source);
}
