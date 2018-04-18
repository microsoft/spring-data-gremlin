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
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
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
    public String generateDeleteAllScript(@Nullable GremlinSource source) {
        return Constants.GREMLIN_SCRIPT_VERTEX_DROP_ALL;
    }

    @Override
    public String generateFindByIdScript(@Nullable GremlinSource source) {
        throw new NotImplementedException("Gremlin graph findById not implemented yet");
    }
}
