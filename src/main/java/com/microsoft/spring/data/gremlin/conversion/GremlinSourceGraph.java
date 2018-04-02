/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.exception.UnexpectedGremlinSourceTypeException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GremlinSourceGraph extends BasicGremlinSource<String> {

    @Getter
    @Setter
    private List<GremlinSource> vertexSet;

    @Getter
    @Setter
    private List<GremlinSource> edgeSet;

    public GremlinSourceGraph() {
        super();

        this.vertexSet = new ArrayList<>();
        this.edgeSet = new ArrayList<>();
    }

    public void addGremlinSource(GremlinSource source) {
        if (source instanceof GremlinSourceVertex) {
            this.vertexSet.add(source);
        } else if (source instanceof GremlinSourceEdge) {
            this.edgeSet.add(source);
        } else {
            throw new UnexpectedGremlinSourceTypeException("source type can only be Vertex or Edge");
        }
    }
}
