/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultReader;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteral;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.driver.Result;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Provider interface to obtain and store information from domain class.
 * For Vertex and Edge, they consist of id (String, Reserved), label (String, Reserved) and
 * a set of properties.
 * The property key should be String, and value can be one of String, number and boolean.
 *
 * @param <T> The type of domain.
 */
public interface GremlinSource<T> {

    /**
     * Set the id of domain
     */
    void setId(Object id);

    /**
     * Set the id of domain
     */
    void setIdField(Field id);

    /**
     * Set the label of domain
     */
    void setLabel(String label);

    /**
     * Set the property map of domain
     */
    void setProperty(String key, Object value);

    /**
     * Get the id of domain
     *
     * @return will never be null
     */
    @NonNull
    Object getId();

    /**
     * Get the id Field of domain
     *
     * @return will never be null
     */
    Field getIdField();

    /**
     * Get the label of domain
     *
     * @return will never be null
     */
    @NonNull
    String getLabel();

    /**
     * Get the Class type of domain
     *
     * @return will never be null
     */
    @NonNull
    Class<T> getDomainClass();

    /**
     * Get the properties of domain
     *
     * @return will never be null
     */
    Map<String, Object> getProperties();

    /**
     * do the real write from domain to GremlinSource
     */
    void doGremlinSourceWrite(Object domain, MappingGremlinConverter converter);

    /**
     * do the real reading from Result to GremlinSource
     */
    void doGremlinResultRead(Result result);

    /**
     * do the real reading from GremlinSource to domain
     */
    T doGremlinSourceRead(Class<T> domainClass, MappingGremlinConverter converter);

    /**
     * return the GremlinScriptLiteral
     */
    GremlinScriptLiteral getGremlinScriptLiteral();

    /**
     * Set the script Strategy of GremlinSource
     */
    void setGremlinScriptStrategy(GremlinScriptLiteral script);

    /**
     * Set the SourceWriter of GremlinSource
     */
    void setGremlinSourceWriter(GremlinSourceWriter writer);

    /**
     * Set the ResultReader for reading data from Gremlin Result to GremlinSource
     */
    void setGremlinResultReader(GremlinResultReader reader);

    /**
     * Set the SourceReader for reading data from GremlinSource to domain
     */
    void setGremlinSourceReader(GremlinSourceReader reader);
}
