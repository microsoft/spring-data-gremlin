/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.query.query.GremlinQuery;

import java.util.List;

/**
 * Provider interface for basic Operations with Gremlin
 */
public interface GremlinOperations {

    void deleteAll();

    void deleteAll(GremlinEntityType type);

    <T> void deleteAll(Class<T> domainClass);

    <T> boolean isEmptyGraph(T object);

    <T> boolean existsById(Object id, Class<T> domainClass);

    <T> void deleteById(Object id, Class<T> domainClass);

    <T> T insert(T object);

    <T> T findById(Object id, Class<T> domainClass);

    <T> T findVertexById(Object id, Class<T> domainClass);

    <T> T findEdgeById(Object id, Class<T> domainClass);

    <T> T update(T object);

    <T> T save(T object);

    <T> List<T> findAll(Class<T> domainClass);

    long vertexCount();

    long edgeCount();

    <T> List<T> find(GremlinQuery query, Class<T> domainClass);

    MappingGremlinConverter getMappingConverter();
}
