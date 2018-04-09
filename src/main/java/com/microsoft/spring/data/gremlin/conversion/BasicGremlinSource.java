/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BasicGremlinSource implements GremlinSource {

    private String id;
    private String label;
    private Map<String, Object> properties;

    @Setter(AccessLevel.PRIVATE)
    private GremlinScript<String> scriptLiteral;

    @Setter(AccessLevel.PRIVATE)
    private GremlinSourceWriter sourceWriter;

    @Setter(AccessLevel.PRIVATE)
    private GremlinResultReader resultReader;

    public BasicGremlinSource() {
        this.properties = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setGremlinScriptStrategy(@NonNull GremlinScript script) {
        this.setScriptLiteral(script);
    }

    @Override
    public void setGremlinSourceWriter(@NonNull GremlinSourceWriter writer) {
        this.setSourceWriter(writer);
    }

    @Override
    public void setGremlinResultReader(@NonNull GremlinResultReader reader) {
        this.setResultReader(reader);
    }

    @Override
    public GremlinScript<String> getGremlinScriptLiteral() {
        return this.scriptLiteral;
    }

    @Override
    public void doGremlinSourceWrite(@NonNull Object domain, @NonNull MappingGremlinConverter converter) {
        Assert.notNull(this.sourceWriter, "the sourceWriter must be set before do writing");

        this.sourceWriter.write(domain, converter, this);
    }

    @Override
    public void doGremlinResultRead(@NonNull Result result) {
        Assert.notNull(this.resultReader, "the resultReader must be set before do reading");

        this.resultReader.read(result, this);
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

