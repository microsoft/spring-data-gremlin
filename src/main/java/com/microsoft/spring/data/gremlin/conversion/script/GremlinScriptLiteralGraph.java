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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class GremlinScriptLiteralGraph implements GremlinScriptLiteral {

    @Override
    public List<String> generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceGraph");
        }

        final List<String> scriptList = new ArrayList<>();
        final GremlinSourceGraph sourceGraph = (GremlinSourceGraph) source;
        final GremlinScriptLiteralVertex scriptVertex = new GremlinScriptLiteralVertex();
        final GremlinScriptLiteralEdge scriptEdge = new GremlinScriptLiteralEdge();

        sourceGraph.getVertexSet().forEach(vertex -> scriptList.addAll(scriptVertex.generateInsertScript(vertex)));
        sourceGraph.getEdgeSet().forEach(edge -> scriptList.addAll(scriptEdge.generateInsertScript(edge)));

        return scriptList;
    }

    @Override
    public List<String> generateDeleteAllScript(@Nullable GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceGraph");
        }

        return Arrays.asList(Constants.GREMLIN_SCRIPT_EDGE_DROP_ALL, Constants.GREMLIN_SCRIPT_VERTEX_DROP_ALL);
    }

    @Override
    public List<String> generateFindByIdScript(@Nullable GremlinSource source) {
        throw new UnsupportedOperationException("Gremlin graph cannot findById by single query.");
    }

    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceGraph");
        }

        final List<String> scriptList = new ArrayList<>();
        final GremlinSourceGraph sourceGraph = (GremlinSourceGraph) source;
        final GremlinScriptLiteralVertex scriptVertex = new GremlinScriptLiteralVertex();
        final GremlinScriptLiteralEdge scriptEdge = new GremlinScriptLiteralEdge();

        sourceGraph.getVertexSet().forEach(vertex -> scriptList.addAll(scriptVertex.generateUpdateScript(vertex)));
        sourceGraph.getEdgeSet().forEach(edge -> scriptList.addAll(scriptEdge.generateUpdateScript(edge)));

        return scriptList;
    }

    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceGraph)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceGraph");
        }

        return this.generateDeleteAllScript(source);
    }

    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        throw new UnsupportedOperationException("Gremlin graph cannot be findAll.");
    }

    public List<String> generateIsEmptyScript(@NonNull GremlinSource source) {
        final List<String> scriptList = new ArrayList<>();

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(Constants.GREMLIN_PRIMITIVE_VERTEX_ALL);

        final String query = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);

        return Collections.singletonList(query);
    }

    @Override
    public List<String> generateCountScript(@NonNull GremlinSource source) {
        throw new UnsupportedOperationException("Gremlin graph counting is not available.");
    }
}
