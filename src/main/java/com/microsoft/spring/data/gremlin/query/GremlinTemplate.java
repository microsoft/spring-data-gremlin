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
import com.microsoft.spring.data.gremlin.exception.GremlinQueryException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
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
import java.util.ArrayList;
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

    @NonNull
    private List<Result> executeQuery(@NonNull List<String> queryList) {
        final Client client = this.gremlinFactory.getGremlinClient();
        final List<Result> results = new ArrayList<>();

        try {
            for (final String query : queryList) {
                results.addAll(client.submit(query).all().join());
            }

            return results;
        } catch (CompletionException e) {
            throw new GremlinQueryException(String.format("unable to complete execute %s from gremlin", queryList), e);
        }
    }

    @Override
    public void deleteAll() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralGraph();
        final List<String> queryList = script.generateDeleteAllScript(null);

        this.executeQuery(queryList);
    }

    @Override
    public <T> T insert(@NonNull T object) {
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(object.getClass());
        final GremlinSource source = information.getGremlinSource();

        this.mappingConverter.write(object, source);

        final List<String> queryList = source.getGremlinScriptLiteral().generateInsertScript(source);

        this.executeQuery(queryList);

        return object;
    }

    @Override
    public <T> T findVertexById(@NonNull Object id, @NonNull Class<T> domainClass) {
        final GremlinEntityInformation information = new GremlinEntityInformation(domainClass);

        if (!information.isEntityVertex()) {
            throw new GremlinUnexpectedEntityTypeException("should be edge domain for findEdge");
        }

        return this.findById(id, domainClass);
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
    public <T> T findEdgeById(@NonNull Object id, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(domainClass);

        if (!information.isEntityEdge()) {
            throw new GremlinUnexpectedEntityTypeException("should be edge domain for findEdge");
        }

        return this.findById(id, domainClass);
    }

    @Override
    public <T> T findById(@NonNull Object id, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(domainClass);
        final GremlinSource source = information.getGremlinSource();

        if (information.isEntityGraph()) {
            throw new UnsupportedOperationException("Gremlin graph cannot be findById.");
        }

        Assert.isTrue(information.isEntityEdge() || information.isEntityVertex(), "only accept vertex or edge");

        source.setId(id.toString());

        final List<String> queryList = source.getGremlinScriptLiteral().generateFindByIdScript(source);
        final List<Result> results = this.executeQuery(queryList);

        if (results.isEmpty()) {
            return null;
        }

        Assert.isTrue(results.size() == 1, "should be only one domain with given id");
        Assert.isTrue(id.toString().equals(source.getId()), "should be the same id");

        source.doGremlinResultRead(results.get(0));
        final T domain = this.mappingConverter.read(domainClass, source);

        if (information.isEntityEdge()) {
            this.completeEdge(domain, (GremlinSourceEdge) source);
        }

        return domain;
    }

    private <T> T updateInternal(@NonNull T object, @NonNull GremlinEntityInformation information) {
        final GremlinSource source = information.getGremlinSource();

        this.mappingConverter.write(object, source);

        final List<String> queryList = source.getGremlinScriptLiteral().generateUpdateScript(source);

        this.executeQuery(queryList);

        return object;
    }

    @Override
    public <T> T update(@NonNull T object) {
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(object.getClass());
        @SuppressWarnings("unchecked")
        final Class<T> domainClass = (Class<T>) object.getClass();

        if (!information.isEntityGraph() && this.findById(information.getId(object), domainClass) == null) {
            throw new GremlinQueryException("cannot update the object doesn't exist");
        }

        return this.updateInternal(object, information);
    }

    @Override
    public <T> T save(@NonNull T object) {
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation information = new GremlinEntityInformation(object.getClass());
        @SuppressWarnings("unchecked")
        final Class<T> domainClass = (Class<T>) object.getClass();

        if (!information.isEntityGraph() && this.findById(information.getId(object), domainClass) == null) {
            return this.insert(object);
        } else {
            return this.updateInternal(object, information);
        }
    }
}

