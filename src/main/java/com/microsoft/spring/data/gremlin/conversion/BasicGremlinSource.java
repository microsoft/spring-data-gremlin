/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BasicGremlinSource<T> implements GremlinSource {

    private String id;
    private String label;
    private Map<String, Object> properties;

    @Setter(AccessLevel.PRIVATE)
    private GremlinScript<T> script;

    @Setter(AccessLevel.PRIVATE)
    private GremlinSourceWriter writer;

    public BasicGremlinSource() {
        this.properties = new HashMap<>();
    }

    public void setGremlinScriptStrategy(@NonNull GremlinScript script) {
        this.setScript(script);
    }

    public void setGremlinSourceWriter(@NonNull GremlinSourceWriter writer) {
        this.setWriter(writer);
    }

    public T generateGremlinScript() {
        return this.script.generateScript(this);
    }

    public void doGremlinSourceWrite(Object domain) {
        this.writer.writeEnityToGremlinSource(domain.getClass(), this);
    }

    private boolean hasProperty(String key) {
        return this.properties.get(key) != null;
    }

    @Override
    public void setProperty(String key, Object value) {
        if (this.hasProperty(key) && value == null) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value);
        }
    }
}
