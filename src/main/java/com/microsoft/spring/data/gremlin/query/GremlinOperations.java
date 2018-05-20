/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;

import java.util.List;

/**
 * Provider interface for basic Operations with Gremlin
 */
public interface GremlinOperations {

    void deleteAll();

    <T> boolean isEmptyGraph(T object);

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

    MappingGremlinConverter getMappingConverter();
}
