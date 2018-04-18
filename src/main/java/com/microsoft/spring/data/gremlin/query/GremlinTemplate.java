/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteral;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.exception.GremlinEntityInformationException;
import com.microsoft.spring.data.gremlin.exception.GremlinFindException;
import com.microsoft.spring.data.gremlin.exception.GremlinInsertionException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
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
        final GremlinScriptLiteral script = new GremlinScriptLiteralGraph();

        client.submit(script.generateDeleteAllScript(null)).all().join();
    }

    @Override
    public <T> T insert(@NonNull T object) {
        final Client client = this.gremlinFactory.getGremlinClient();
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(object.getClass());
        final GremlinSource source = information.getGremlinSource();
        final GremlinScriptLiteral script = source.getGremlinScriptLiteral();

        this.mappingConverter.write(object, source);

        try {
            client.submit(script.generateInsertScript(source)).all().join();
        } catch (CompletionException e) {
            final String typeName = object.getClass().getName();
            throw new GremlinInsertionException(String.format("unable to insert type %s from gremlin", typeName), e);
        }

        return object;
    }

    @Override
    public <T> T findVertexById(@NonNull Object id, @NonNull Class<T> domainClass) {
        final List<Result> results;
        final Client client = this.gremlinFactory.getGremlinClient();
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(domainClass);
        final GremlinSource source = information.getGremlinSource();

        source.setId(id.toString());

        try {
            results = client.submit(source.getGremlinScriptLiteral().generateFindByIdScript(source)).all().join();
        } catch (CompletionException e) {
            final String typeName = domainClass.getName();
            throw new GremlinFindException(String.format("unable to complete find %s from gremlin", typeName), e);
        }

        if (results.isEmpty()) {
            return null;
        }

        Assert.isTrue(results.size() == 1, "should be only one vertex with given id");
        Assert.isTrue(id.toString().equals(source.getId()), "should be the same id");

        source.doGremlinResultRead(results.get(0));
        final T domain = this.mappingConverter.read(domainClass, source);

        return domain;
    }

    /**
     * Find Edge need another two query to obtain edgeFrom and edgeTo.
     * This function will do that and make edge domain completion.
     */
    private <T> void completeEdge(@NonNull T domain, @NonNull GremlinSourceEdge source) {
        final ConvertingPropertyAccessor accessor = this.mappingConverter.getPropertyAccessor(domain);
        final GremlinPersistentEntity persistentEntity = this.mappingConverter.getPersistentEntity(domain.getClass());

        final List<Field> fromFields = FieldUtils.getFieldsListWithAnnotation(domain.getClass(), EdgeFrom.class);
        final List<Field> toFields = FieldUtils.getFieldsListWithAnnotation(domain.getClass(), EdgeTo.class);

        if (fromFields.size() != 1 || toFields.size() != 1) {
            throw new GremlinEntityInformationException("should be only one Annotation");
        }

        final Field fromField = fromFields.get(0);
        final Field toField = toFields.get(0);

        final Object vertexFrom = this.findVertexById(source.getVertexIdFrom(), fromField.getType());
        final Object vertexTo = this.findVertexById(source.getVertexIdTo(), toField.getType());

        final PersistentProperty propertyFrom = persistentEntity.getPersistentProperty(fromField.getName());
        final PersistentProperty propertyTo = persistentEntity.getPersistentProperty(toField.getName());

        Assert.notNull(propertyFrom, "persistence property should not be null");
        Assert.notNull(propertyTo, "persistence property should not be null");

        accessor.setProperty(propertyFrom, vertexFrom);
        accessor.setProperty(propertyTo, vertexTo);
    }

    @Override
    public <T> T findById(@NonNull Object id, @NonNull Class<T> domainClass) {

        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(domainClass);

        switch (information.getEntityType()) {
            case EDGE:
                return this.findEdgeById(id, domainClass);
            case VERTEX:
                return this.findVertexById(id, domainClass);
            case GRAPH:
            default:
                throw new UnsupportedOperationException("not implemented yet");
        }
    }

    @Override
    public <T> T findEdgeById(@NonNull Object id, @NonNull Class<T> domainClass) {
        final List<Result> results;
        final Client client = this.gremlinFactory.getGremlinClient();
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(domainClass);
        final GremlinSource source = information.getGremlinSource();

        source.setId(id.toString());

        try {
            results = client.submit(source.getGremlinScriptLiteral().generateFindByIdScript(source)).all().join();
        } catch (CompletionException e) {
            final String typeName = domainClass.getName();
            throw new GremlinFindException(String.format("unable to complete find %s from gremlin", typeName), e);
        }

        if (results.isEmpty()) {
            return null;
        }

        Assert.isTrue(results.size() == 1, "should be only one edge with given id");
        Assert.isTrue(id.toString().equals(source.getId()), "should be the same id");

        source.doGremlinResultRead(results.get(0));
        final T domain = this.mappingConverter.read(domainClass, source);
        this.completeEdge(domain, (GremlinSourceEdge) source);

        return domain;
    }
}

