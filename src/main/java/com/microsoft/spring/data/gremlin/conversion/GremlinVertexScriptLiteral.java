/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

@NoArgsConstructor
public class GremlinVertexScriptLiteral implements GremlinScript<String> {

    @Override
    public String generateScript(GremlinSource gremlinSource) {
        throw new NotImplementedException("generate literal vertex script is not implemented");
    }
}
