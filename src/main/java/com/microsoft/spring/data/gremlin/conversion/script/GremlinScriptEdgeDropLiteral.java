/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GremlinScriptEdgeDropLiteral implements GremlinScript<String> {

    @Override
    public String generateScript(GremlinSource source) {
        return Constants.GREMLIN_SCRIPT_EDGE_DROP;
    }
}

