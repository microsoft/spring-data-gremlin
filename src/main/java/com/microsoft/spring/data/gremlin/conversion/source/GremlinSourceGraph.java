/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;

import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultsGraphReader;
import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultsReader;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralGraph;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;

import lombok.Getter;

public class GremlinSourceGraph extends AbstractGremlinSource {

    @Getter
    private List<GremlinSource> vertexSet;

    @Getter
    private List<GremlinSource> edgeSet;
    
    @Getter
    private GremlinResultsReader resultsReader;

    public GremlinSourceGraph() {
        super();
        this.setGremlinScriptStrategy(new GremlinScriptLiteralGraph());
        this.setGremlinSourceWriter(new GremlinSourceGraphWriter());
        this.setGremlinSourceReader(new GremlinSourceGraphReader());
        this.resultsReader = new GremlinResultsGraphReader();

        this.vertexSet = new ArrayList<>();
        this.edgeSet = new ArrayList<>();
    }

    public void addGremlinSource(GremlinSource source) {
        if (source instanceof GremlinSourceVertex) {
            this.vertexSet.add(source);
        } else if (source instanceof GremlinSourceEdge) {
            this.edgeSet.add(source);
        } else {
            throw new GremlinUnexpectedSourceTypeException("source type can only be Vertex or Edge");
        }
    }
}

