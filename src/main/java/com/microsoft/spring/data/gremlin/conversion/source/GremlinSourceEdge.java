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

public class GremlinSourceEdge<T> extends AbstractGremlinSource<T> {

    @Getter
    @Setter
    private Object vertexIdFrom;

    @Getter
    @Setter
    private Object vertexIdTo;

    public GremlinSourceEdge() {
        super();
        initializeGremlinStrategy();
    }

    public GremlinSourceEdge(Class<T> domainClass) {
        super(domainClass);
        initializeGremlinStrategy();
    }

    private void initializeGremlinStrategy() {
        this.setGremlinScriptStrategy(new GremlinScriptLiteralEdge());
        this.setGremlinResultReader(new GremlinResultEdgeReader());
        this.setGremlinSourceReader(new GremlinSourceEdgeReader());
        this.setGremlinSourceWriter(new GremlinSourceEdgeWriter());
    }
}
