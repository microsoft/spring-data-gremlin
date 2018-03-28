/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;

@NoArgsConstructor
public class GremlinScriptTraversal implements GremlinScript<GraphTraversal> {

    @Override
    public GraphTraversal generateScript(GremlinSource gremlinSource) {
        throw new NotImplementedException("generate traversal script is not implemented");
    }
}
