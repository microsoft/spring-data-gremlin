/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common.domain;

import com.microsoft.spring.data.gremlin.annotation.EdgeSet;
import com.microsoft.spring.data.gremlin.annotation.Graph;
import com.microsoft.spring.data.gremlin.annotation.VertexSet;
import com.microsoft.spring.data.gremlin.common.TestConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Graph(collection = TestConstants.GRAPH_ROADMAP_COLLECTION_NAME)
public class Roadmap {

    @Getter
    @Setter
    private String id;

    @VertexSet
    private List<Object> vertexList;

    @EdgeSet
    private List<Object> edgeList;

    public Roadmap() {
        this.vertexList = new ArrayList<>();
        this.edgeList = new ArrayList<>();
    }

    public void vertexAdd(Object object) {
        this.vertexList.add(object);
    }

    public void edgeAdd(Object object) {
        this.edgeList.add(object);
    }
}
