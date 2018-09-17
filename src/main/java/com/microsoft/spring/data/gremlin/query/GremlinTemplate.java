/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteral;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralEdge;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralGraph;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralVertex;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinEntityInformationException;
import com.microsoft.spring.data.gremlin.exception.GremlinQueryException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import com.microsoft.spring.data.gremlin.query.query.GremlinQuery;
import com.microsoft.spring.data.gremlin.query.query.QueryFindScriptGenerator;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;


public class GremlinTemplate implements GremlinOperations, ApplicationContextAware {

    private final GremlinFactory factory;
    private final MappingGremlinConverter mappingConverter;

    private Client gremlinClient;
    private ApplicationContext context;

    public GremlinTemplate(@NonNull GremlinFactory factory, @NonNull MappingGremlinConverter converter) {
        this.factory = factory;
        this.mappingConverter = converter;
    }

    @Override
    public MappingGremlinConverter getMappingConverter() {
        return this.mappingConverter;
    }

    public ApplicationContext getApplicationContext() {
        return this.context;
    }

    public Client getGremlinClient() {
        if (this.gremlinClient == null) {
            this.gremlinClient = this.factory.getGremlinClient();
        }

        return this.gremlinClient;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @NonNull
    private List<Result> executeQuery(@NonNull List<String> queryList) {
        final List<Result> results = new ArrayList<>();

        try {
            queryList.forEach(query -> results.addAll(this.getGremlinClient().submit(query).all().join()));
            return results;
        } catch (CompletionException e) {
            throw new GremlinQueryException(String.format("unable to complete execute %s from gremlin", queryList), e);
        }
    }

    @Override
    public void deleteAll() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralGraph();
        final List<String> queryList = script.generateDeleteAllScript(new GremlinSourceGraph());

        this.executeQuery(queryList);
    }

    @Override
    public void deleteAll(GremlinEntityType type) {
        if (type == GremlinEntityType.UNKNOWN) {
            throw new GremlinUnexpectedEntityTypeException("must be explicit entity type");
        }

        if (type != GremlinEntityType.EDGE) {
            this.deleteAll();
        }

        final GremlinSource source = new GremlinSourceEdge();

        source.setGremlinScriptStrategy(new GremlinScriptLiteralEdge());

        final List<String> queryList = source.getGremlinScriptLiteral().generateDeleteAllScript(source);

        this.executeQuery(queryList);
    }

    public <T> void deleteAll(@NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        final GremlinSource source = info.getGremlinSource();
        final List<String> queryList = source.getGremlinScriptLiteral().generateDeleteAllByClassScript(source);

        this.executeQuery(queryList);
    }

    @Override
    public <T> T insert(@NonNull T object) {
        final Class domainClass = object.getClass();
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        final GremlinSource source = info.getGremlinSource();

        this.mappingConverter.write(object, source);

        final List<String> queryList = source.getGremlinScriptLiteral().generateInsertScript(source);

        this.executeQuery(queryList);

        return object;
    }

    @Override
    public <T> T findVertexById(@NonNull Object id, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);

        if (!info.isEntityVertex()) {
            throw new GremlinUnexpectedEntityTypeException("should be vertex domain for findEdge");
        }

        return this.findById(id, domainClass);
    }

    private Object getEdgeAnnotatedFieldValue(@NonNull Field field, @NonNull Object vertexId) {
        if (field.getType() == String.class || field.getType() == Long.class || field.getType() == Integer.class) {
            return vertexId;
        } else if (field.getType().isPrimitive()) {
            throw new GremlinUnexpectedEntityTypeException("only String type of primitive is allowed");
        } else {
            return this.findVertexById(vertexId, field.getType());
        }
    }

    @NonNull
    private Field getEdgeAnnotatedField(@NonNull Class<?> domainClass,
                                        @NonNull Class<? extends Annotation> annotationClass) {
        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainClass, annotationClass);

        if (fields.size() != 1) {
            throw new GremlinEntityInformationException("should be only one Annotation");
        }

        return fields.get(0);
    }

    /**
     * Find Edge need another two query to obtain edgeFrom and edgeTo.
     * This function will do that and make edge domain completion.
     */
    private <T> void completeEdge(@NonNull T domain, @NonNull GremlinSourceEdge source) {
        final ConvertingPropertyAccessor accessor = this.mappingConverter.getPropertyAccessor(domain);
        final GremlinPersistentEntity persistentEntity = this.mappingConverter.getPersistentEntity(domain.getClass());

        final Field fromField = this.getEdgeAnnotatedField(domain.getClass(), EdgeFrom.class);
        final Field toField = this.getEdgeAnnotatedField(domain.getClass(), EdgeTo.class);

        final PersistentProperty propertyFrom = persistentEntity.getPersistentProperty(fromField.getName());
        final PersistentProperty propertyTo = persistentEntity.getPersistentProperty(toField.getName());

        Assert.notNull(propertyFrom, "persistence property should not be null");
        Assert.notNull(propertyTo, "persistence property should not be null");

        accessor.setProperty(propertyFrom, this.getEdgeAnnotatedFieldValue(fromField, source.getVertexIdFrom()));
        accessor.setProperty(propertyTo, this.getEdgeAnnotatedFieldValue(toField, source.getVertexIdTo()));
    }

    @Override
    public <T> T findEdgeById(@NonNull Object id, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);

        if (!info.isEntityEdge()) {
            throw new GremlinUnexpectedEntityTypeException("should be edge domain for findEdge");
        }

        return this.findById(id, domainClass);
    }

    @Override
    public <T> T findById(@NonNull Object id, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        final GremlinSource source = info.getGremlinSource();

        if (info.isEntityGraph()) {
            throw new UnsupportedOperationException("Gremlin graph cannot be findById.");
        }

        Assert.isTrue(info.isEntityEdge() || info.isEntityVertex(), "only accept vertex or edge");

        source.setId(id);

        final List<String> queryList = source.getGremlinScriptLiteral().generateFindByIdScript(source);
        final List<Result> results = this.executeQuery(queryList);

        if (results.isEmpty()) {
            return null;
        }

        Assert.isTrue(results.size() == 1, "should be only one domain with given id");

        return this.recoverDomain(source, results.get(0), domainClass, info.isEntityEdge());
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
        @SuppressWarnings("unchecked") final Class<T> domainClass = (Class<T>) object.getClass();
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        @SuppressWarnings("unchecked") final Object id = info.getId(object);

        if (!info.isEntityGraph() && this.findById(id, domainClass) == null) {
            throw new GremlinQueryException("cannot update the object doesn't exist");
        }

        return this.updateInternal(object, info);
    }

    @Override
    public <T> T save(@NonNull T object) {
        @SuppressWarnings("unchecked") final Class<T> domainClass = (Class<T>) object.getClass();
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        @SuppressWarnings("unchecked") final Object id = info.getId(object);

        if (info.isEntityGraph() && this.isEmptyGraph(object)) {
            return this.insert(object);
        } else if (!info.isEntityGraph() && this.findById(id, domainClass) == null) {
            return this.insert(object);
        } else {
            return this.updateInternal(object, info);
        }
    }

    @Override
    public <T> List<T> findAll(@NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        final GremlinSource source = info.getGremlinSource();

        if (info.isEntityGraph()) {
            throw new UnsupportedOperationException("Gremlin graph cannot be findAll.");
        }

        final List<String> queryList = source.getGremlinScriptLiteral().generateFindAllScript(source);
        final List<Result> results = this.executeQuery(queryList);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        return this.recoverDomainList(source, results, domainClass, info.isEntityEdge());
    }

    @Override
    public <T> void deleteById(@NonNull Object id, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        final GremlinSource source = info.getGremlinSource();

        source.setId(id);

        final List<String> queryList = source.getGremlinScriptLiteral().generateDeleteByIdScript(source);

        this.executeQuery(queryList);
    }

    @Override
    public <T> boolean isEmptyGraph(@NonNull T object) {
        @SuppressWarnings("unchecked") final Class<T> domainClass = (Class<T>) object.getClass();
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);

        if (!info.isEntityGraph()) {
            throw new GremlinQueryException("only graph domain is allowed.");
        }

        final GremlinSource source = info.getGremlinSource();
        final GremlinScriptLiteralGraph literalGraph = (GremlinScriptLiteralGraph) source.getGremlinScriptLiteral();
        final List<String> queryList = literalGraph.generateIsEmptyScript();
        final List<Result> results = this.executeQuery(queryList);

        return results.size() == 0;
    }

    @Override
    public long vertexCount() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralVertex();
        final List<String> queryList = script.generateCountScript(new GremlinSourceVertex());
        final List<Result> results = this.executeQuery(queryList);

        return results.size();
    }

    @Override
    public long edgeCount() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralEdge();
        final List<String> queryList = script.generateCountScript(new GremlinSourceEdge());
        final List<Result> results = this.executeQuery(queryList);

        return results.size();
    }

    private <T> T recoverDomain(@NonNull GremlinSource source, @NonNull Result result,
                                @NonNull Class<T> domainClass, boolean isEntityEdge) {
        final T domain;

        source.doGremlinResultRead(result);
        domain = this.mappingConverter.read(domainClass, source);

        if (isEntityEdge) {
            this.completeEdge(domain, (GremlinSourceEdge) source);
        }

        return domain;
    }

    private <T> List<T> recoverDomainList(@NonNull GremlinSource source, @NonNull List<Result> results,
                                          @NonNull Class<T> domainClass, boolean isEntityEdge) {
        final List<T> domainList = new ArrayList<>();

        results.forEach(s -> domainList.add(this.recoverDomain(source, s, domainClass, isEntityEdge)));

        return domainList;
    }

    @Override
    public <T> List<T> find(@NonNull GremlinQuery query, @NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = new GremlinEntityInformation(domainClass);
        final GremlinSource source = info.getGremlinSource();

        query.setScriptGenerator(new QueryFindScriptGenerator());

        final List<String> queryList = query.doSentenceGenerate(domainClass);
        final List<Result> results = this.executeQuery(queryList);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        return this.recoverDomainList(source, results, domainClass, info.isEntityEdge());
    }
}

