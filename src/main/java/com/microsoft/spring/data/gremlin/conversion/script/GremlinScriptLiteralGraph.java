/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class GremlinScriptLiteralGraph implements GremlinScriptLiteral {

    private String trimScriptHead(@NonNull String script) {
        return script.replaceFirst(Constants.GREMLIN_SCRIPT_HEAD, "");
    }

    @Override
    public String generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceGraph");
        }

        final List<String> scriptList = new ArrayList<>();
        final GremlinSourceGraph sourceGraph = (GremlinSourceGraph) source;

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);

        for (final GremlinSource vertex : sourceGraph.getVertexSet()) {
            final String vertexScript = new GremlinScriptLiteralVertex().generateInsertScript(vertex);
            scriptList.add(this.trimScriptHead(vertexScript));
        }

        for (final GremlinSource edge : sourceGraph.getEdgeSet()) {
            final String edgeScript = new GremlinScriptLiteralEdge().generateInsertScript(edge);
            scriptList.add(this.trimScriptHead(edgeScript));
        }

        return String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);
    }

    @Override
    public List<String> generateDeleteAllScript(@Nullable GremlinSource source) {
        return Arrays.asList(Constants.GREMLIN_SCRIPT_EDGE_DROP_ALL, Constants.GREMLIN_SCRIPT_VERTEX_DROP_ALL);
    }

    @Override
    public String generateFindByIdScript(@Nullable GremlinSource source) {
        throw new UnsupportedOperationException("Gremlin graph cannot findById by single query.");
    }

    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceGraph");
        }

        final List<String> scriptVertex = new ArrayList<>();
        final List<String> scriptEdge = new ArrayList<>();
        final GremlinSourceGraph sourceGraph = (GremlinSourceGraph) source;

        scriptVertex.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptEdge.add(Constants.GREMLIN_PRIMITIVE_GRAPH);

        for (final GremlinSource vertex : sourceGraph.getVertexSet()) {
            final List<String> vertexScript = new GremlinScriptLiteralVertex().generateUpdateScript(vertex);
            Assert.isTrue(vertexScript.size() == 1, "should be only one query");

            scriptVertex.add(this.trimScriptHead(vertexScript.get(0)));
        }

        for (final GremlinSource edge : sourceGraph.getEdgeSet()) {
            final List<String> edgeScript = new GremlinScriptLiteralEdge().generateUpdateScript(edge);
            Assert.isTrue(edgeScript.size() == 1, "should be only one query");

            scriptEdge.add(this.trimScriptHead(edgeScript.get(0)));
        }

        final String queryVertex = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptVertex);
        final String queryEdge = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptEdge);

        return Arrays.asList(queryVertex, queryEdge);
    }
}
