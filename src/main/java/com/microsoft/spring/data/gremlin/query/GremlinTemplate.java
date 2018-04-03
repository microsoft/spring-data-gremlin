/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.*;
import com.microsoft.spring.data.gremlin.exception.GremlinInsertionException;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;


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
    public <T> T insert(T object) {
        final Client client = this.gremlinFactory.getGremlinClient();
        final GremlinEntityInformation information = new GremlinEntityInformation(object.getClass());
        final GremlinSource source = information.getGremlinSource();
        final GremlinScript<String> script = source.getGremlinScriptLiteral();

        source.doGremlinSourceWrite(object, this.mappingConverter);

        try {
            client.submit(script.generateScript(source)).all().join();
        } catch (RuntimeException e) {
            final String typeName = object.getClass().getName();
            throw new GremlinInsertionException(String.format("unable to insert type %s from gremlin", typeName), e);
        }

        return object;
    }
}
