/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.*;
import com.microsoft.spring.data.gremlin.exception.GremlinFindException;
import com.microsoft.spring.data.gremlin.exception.GremlinInsertionException;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.CompletionException;


public class GremlinTemplate implements GremlinOperations, ApplicationContextAware {

    private final GremlinFactory gremlinFactory;
    private final MappingGremlinConverter mappingConverter;
    private ApplicationContext context;

    public GremlinTemplate(@NonNull GremlinFactory factory, @NonNull MappingGremlinConverter converter) {
        this.gremlinFactory = factory;
        this.mappingConverter = converter;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public ApplicationContext getApplicationContext() {
        return this.context;
    }

    @Override
    public void deleteAll() {
        final Client client = this.gremlinFactory.getGremlinClient();

        client.submit(new GremlinScriptEdgeDropLiteral().generateScript(null)).all().join();
        client.submit(new GremlinScriptVertexDropLiteral().generateScript(null)).all().join();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T insert(@NonNull T object) {
        final Client client = this.gremlinFactory.getGremlinClient();
        final GremlinEntityInformation information = new GremlinEntityInformation(object.getClass());
        final GremlinSource source = information.getGremlinSource();
        final GremlinScript<String> script = source.getGremlinScriptLiteral();

        this.mappingConverter.write(object, source);

        try {
            client.submit(script.generateScript(source)).all().join();
        } catch (CompletionException e) {
            final String typeName = object.getClass().getName();
            throw new GremlinInsertionException(String.format("unable to insert type %s from gremlin", typeName), e);
        }

        return object;
    }

    @Override
    public <T> T findVertexById(@NonNull Object id, @NonNull Class<T> domainClass) {
        final Client client = this.gremlinFactory.getGremlinClient();
        final String idValue = id.toString();
        final GremlinSource source = new GremlinSourceVertex(idValue);
        final GremlinScript<String> script = new GremlinScriptVertexFindByIdLiteral();
        final List<Result> results;

        try {
            results = client.submit(script.generateScript(source)).all().join();
        } catch (CompletionException e) {
            final String typeName = domainClass.getName();
            throw new GremlinFindException(String.format("unable to complete find %s from gremlin", typeName), e);
        }

        if (results.isEmpty()) {
            return null;
        }

        source.setGremlinResultReader(new GremlinResultVertexReader());
        source.setGremlinSourceReader(new GremlinSourceVertexReader());

        source.doGremlinResultRead(results.get(0));
        Assert.isTrue(results.size() == 1, "should be only 1 one vertex with given id");

        return this.mappingConverter.read(domainClass, source);
    }
}
