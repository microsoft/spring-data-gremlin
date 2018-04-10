/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.result;

import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import org.apache.tinkerpop.gremlin.driver.Result;

public interface GremlinResultReader {
    /**
     * Read the Gremlin returned Result to GremlinSource.
     */
    void read(Result result, GremlinSource source);
}
