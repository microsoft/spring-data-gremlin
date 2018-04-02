/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.common.Constants;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class GremlinScriptGraphLiteral implements GremlinScript<String> {

    private String trimScriptHead(@NonNull String script) {
        return script.replaceFirst(Constants.GREMLIN_SCRIPT_HEAD, "") ;
    }

    @Override
    public String generateScript(@NonNull GremlinSource source) {
        Assert.isTrue(source instanceof GremlinSourceGraph, "should be Graph extend from GremlinSource");

        final List<String> scriptList = new ArrayList<>();
        final GremlinSourceGraph sourceGraph = (GremlinSourceGraph) source;

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);

        for (final GremlinSource vertex : sourceGraph.getVertexSet()) {
            String vertexScript = new GremlinScriptVertexLiteral().generateScript(vertex);
            scriptList.add(this.trimScriptHead(vertexScript));
        }

        for (final GremlinSource edge: sourceGraph.getEdgeSet()) {
            String edgeScript = new GremlinScriptEdgeLiteral().generateScript(edge);
            scriptList.add(this.trimScriptHead(edgeScript));
        }

        return String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);
    }
}
