/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.result;

import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;

public class GremlinResultsGraphReader extends AbstractGremlinResultReader implements GremlinResultsReader {
    
    private GremlinResultVertexReader vertexResultReader;
    private GremlinResultEdgeReader edgeResultReader;

    public GremlinResultsGraphReader() {
        vertexResultReader = new GremlinResultVertexReader();
        edgeResultReader = new GremlinResultEdgeReader();
    }

    @Override
    public void read(@NonNull List<Result> results, @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("Should be instance of GremlinSourceGraph");
        }
        final GremlinSourceGraph graphSource = (GremlinSourceGraph) source;
        
        for (final Result result : results) {
            processResult(result, graphSource);
        }
    }
    
    private void processResult(Result result, GremlinSourceGraph graphSource) {
        
        final Object obj = result.getObject();
        Assert.isInstanceOf(Map.class, obj, "should be an instance of Map");
        @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) result.getObject();
        Assert.isTrue(map.containsKey(Constants.PROPERTY_TYPE), "should contain a type property");
        final String type = (String) map.get(Constants.PROPERTY_TYPE);
        GremlinSource resultSource;
        if (type.equals(Constants.RESULT_TYPE_VERTEX)) {
            resultSource = new GremlinSourceVertex();
            vertexResultReader.read(result, resultSource);
        } else if (type.equals(Constants.RESULT_TYPE_EDGE)) {
            resultSource = new GremlinSourceEdge();
            edgeResultReader.read(result, resultSource);
        } else {
            throw new GremlinUnexpectedEntityTypeException("Unexpected result type: " + type);
        }
        
        graphSource.addGremlinSource(resultSource);
    }
}
