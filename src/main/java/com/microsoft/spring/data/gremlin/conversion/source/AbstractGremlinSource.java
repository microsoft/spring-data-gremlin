/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.annotation.GeneratedValue;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultsReader;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteral;
import com.microsoft.spring.data.gremlin.exception.GremlinInvalidEntityIdFieldException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.microsoft.spring.data.gremlin.common.Constants.GREMLIN_PROPERTY_CLASSNAME;

public abstract class AbstractGremlinSource<T> implements GremlinSource<T> {

    @Setter
    private Object id;

    @Getter
    @Setter
    private String label;

    @Getter
    @Setter
    private Field idField;

    @Getter
    private Class<T> domainClass;

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
    private GremlinResultsReader resultReader;

    protected AbstractGremlinSource() {
        this.properties = new HashMap<>();
    }

    protected AbstractGremlinSource(Class<T> domainClass) {
        this.domainClass = domainClass;
        this.properties = new HashMap<>();

        setProperty(GREMLIN_PROPERTY_CLASSNAME, domainClass.getName());
    }

    @Override
    public Optional<Object> getId() {
        return Optional.ofNullable(this.id);
    }

    /**
     * The type of Id keep the consistency with the result from gremlin server, for generate query correctly. So if the
     * id is ${@link GeneratedValue}, which may have different type against entity id field.
     *
     * @param id the given id from query.
     */
    @Override
    public void setId(Object id) {
        final Field idField = getIdField();

        if (idField == null) {
            throw new GremlinInvalidEntityIdFieldException("Id Field of GremlinSource cannot be null");
        }

        if (idField.isAnnotationPresent(GeneratedValue.class) && id instanceof String) {
            try {
                this.id = Long.valueOf((String) id); // Gremlin server default id type is Long.
            } catch (NumberFormatException ignore) {
                this.id = id;
            }
        } else {
            this.id = id;
        }
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
    public void setGremlinResultReader(@NonNull GremlinResultsReader reader) {
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
    public T doGremlinSourceRead(@NonNull Class<T> domainClass, @NonNull MappingGremlinConverter converter) {
        Assert.notNull(this.sourceReader, "the sourceReader must be set before do reading");

        return this.sourceReader.read(domainClass, converter, this);
    }

    @Override
    public void doGremlinResultRead(@NonNull List<Result> results) {
        Assert.notNull(this.resultReader, "the resultReader must be set before do reading");

        this.resultReader.read(results, this);
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

