/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import lombok.Getter;
import lombok.Setter;

public class GremlinSourceEdge extends BasicGremlinSource {

    @Getter
    @Setter
    private String vertexIdFrom;

    @Getter
    @Setter
    private String vertexIdTo;

    public GremlinSourceEdge() {
        super();
    }
}
