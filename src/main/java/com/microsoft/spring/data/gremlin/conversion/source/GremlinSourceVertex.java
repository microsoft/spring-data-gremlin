/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultVertexReader;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralVertex;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;

public class GremlinSourceVertex extends AbstractGremlinSource {

    public GremlinSourceVertex() {
        super();
        this.setGremlinScriptStrategy(new GremlinScriptLiteralVertex());
        this.setGremlinResultReader(new GremlinResultVertexReader());
        this.setGremlinSourceReader(new GremlinSourceVertexReader());
        this.setGremlinSourceWriter(new GremlinSourceVertexWriter());
    }
}
