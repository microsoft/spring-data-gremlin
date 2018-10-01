/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultReader;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteral;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGremlinSource implements GremlinSource {

    @Getter
    @Setter
    private Object id;

    @Getter
    @Setter
    private String label;

    @Getter
    @Setter
    private Field idField;

    @Getter
    @Setter
    private Class<?> domainClass;

    @Getter
    @Setter
    private Map<String, Object> properties;

    @Setter(AccessLevel.PRIVATE)
    private GremlinScriptLiteral scriptLiteral;

    @Setter(AccessLevel.PRIVATE)
    private GremlinSourceWriter sourceWriter;

    @Setter(AccessLevel.PRIVATE)
    private GremlinSourceReader sourceReader;

    @Setter(AccessLevel.PRIVATE)
    private GremlinResultReader resultReader;

    protected AbstractGremlinSource() {
        this.properties = new HashMap<>();
    }

    @Override
    public void setGremlinScriptStrategy(@NonNull GremlinScriptLiteral script) {
        this.setScriptLiteral(script);
    }

    @Override
    public void setGremlinSourceWriter(@NonNull GremlinSourceWriter writer) {
        this.setSourceWriter(writer);
    }

    @Override
    public void setGremlinSourceReader(@NonNull GremlinSourceReader reader) {
        this.setSourceReader(reader);
    }

    @Override
    public void setGremlinResultReader(@NonNull GremlinResultReader reader) {
        this.setResultReader(reader);
    }

    @Override
    public GremlinScriptLiteral getGremlinScriptLiteral() {
        return this.scriptLiteral;
    }

    @Override
    public void doGremlinSourceWrite(@NonNull Object domain, @NonNull MappingGremlinConverter converter) {
        Assert.notNull(this.sourceWriter, "the sourceWriter must be set before do writing");

        this.sourceWriter.write(domain, converter, this);
    }

    @Override
    public <T> T doGremlinSourceRead(@NonNull Class<T> domainClass, @NonNull MappingGremlinConverter converter) {
        Assert.notNull(this.sourceReader, "the sourceReader must be set before do reading");

        return this.sourceReader.read(domainClass, converter, this);
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

