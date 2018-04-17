/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptGraphLiteral;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GremlinSourceGraph extends BasicGremlinSource {

    @Getter
    @Setter
    private List<GremlinSource> vertexSet;

    @Getter
    @Setter
    private List<GremlinSource> edgeSet;

    public GremlinSourceGraph() {
        super();
        this.setGremlinScriptStrategy(new GremlinScriptGraphLiteral());
        this.setGremlinSourceWriter(new GremlinSourceGraphWriter());

        this.vertexSet = new ArrayList<>();
        this.edgeSet = new ArrayList<>();
    }

    public void addGremlinSource(GremlinSource source) {
        if (source instanceof GremlinSourceVertex) {
            this.vertexSet.add(source);
        } else if (source instanceof GremlinSourceEdge) {
            this.edgeSet.add(source);
        } else {
            throw new GremlinUnexpectedSourceTypeException("source type can only be Vertex or Edge");
        }
    }

    public GremlinSourceGraph(@NonNull Field idField, @NonNull String label) {
        this();
        super.setIdField(idField);
        super.setLabel(label);
    }
}

