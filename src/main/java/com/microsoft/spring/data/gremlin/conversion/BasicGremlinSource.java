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

    public BasicGremlinSource() {
        this.id = null;
        this.label = null;
        this.properties = new HashMap<>();
        this.script = null;
    }

    public void setGremlinScriptStrategy(@NonNull GremlinScript script) {
        this.setScript(script);
    }

    public T generateGremlinScript() {
        return this.script.generateScript(this);
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
