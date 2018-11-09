/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.result;

import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static com.microsoft.spring.data.gremlin.common.Constants.*;
import static java.util.Collections.singletonList;

public class GremlinResultsGraphReader extends AbstractGremlinResultReader implements GremlinResultsReader {

    private final GremlinResultVertexReader vertexResultReader;
    private final GremlinResultEdgeReader edgeResultReader;

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

        graphSource.getVertexSet().clear();
        graphSource.getEdgeSet().clear();

        results.stream().map(this::processResult).forEach(graphSource::addGremlinSource);
    }

    private GremlinSource processResult(Result result) {
        final GremlinSource source;
        final Object obj = result.getObject();

        Assert.isInstanceOf(Map.class, obj, "should be an instance of Map");
        @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) result.getObject();

        Assert.isTrue(map.containsKey(PROPERTY_TYPE), "should contain a type property");
        final String type = (String) map.get(PROPERTY_TYPE);

        switch (type) {
            case RESULT_TYPE_VERTEX:
                source = new GremlinSourceVertex();
                vertexResultReader.read(singletonList(result), source);
                break;
            case RESULT_TYPE_EDGE:
                source = new GremlinSourceEdge();
                edgeResultReader.read(singletonList(result), source);
                break;
            default:
                throw new GremlinUnexpectedEntityTypeException("Unexpected result type: " + type);
        }

        return source;
    }
}
