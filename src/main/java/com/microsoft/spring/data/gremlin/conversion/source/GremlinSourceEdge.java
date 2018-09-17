/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultEdgeReader;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralEdge;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;

public class GremlinSourceEdge extends AbstractGremlinSource {

    @Getter
    @Setter
    private Object vertexIdFrom;

    @Getter
    @Setter
    private Object vertexIdTo;

    public GremlinSourceEdge() {
        super();
        this.setGremlinScriptStrategy(new GremlinScriptLiteralEdge());
        this.setGremlinResultReader(new GremlinResultEdgeReader());
        this.setGremlinSourceReader(new GremlinSourceEdgeReader());
        this.setGremlinSourceWriter(new GremlinSourceEdgeWriter());
    }

    public GremlinSourceEdge(@NonNull Field idField, @NonNull String label) {
        this();
        super.setIdField(idField);
        super.setLabel(label);
    }
}
