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
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
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
import com.microsoft.spring.data.gremlin.query.query.QueryScriptGenerator;
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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


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

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public Client getGremlinClient() {
        if (this.gremlinClient == null) {
            this.gremlinClient = this.factory.getGremlinClient();
        }

        return this.gremlinClient;
    }

    @NonNull
    private List<Result> executeQuery(@NonNull List<String> queries) {
        final List<List<String>> parallelQueries = GremlinUtils.toParallelQueryList(queries);

        return parallelQueries.stream().flatMap(q -> executeQueryParallel(q).stream()).collect(Collectors.toList());
    }

    @NonNull
    private List<Result> executeQueryParallel(@NonNull List<String> queries) {
        return queries.parallelStream()
                .map(q -> getGremlinClient().submit(q).all())
                .collect(toList()).parallelStream().flatMap(f -> {
                    try {
                        return f.get().stream();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new GremlinQueryException("unable to complete query from gremlin", e);
                    }
                }).collect(toList());
    }

    @Override
    public void deleteAll() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralGraph();
        final List<String> queryList = script.generateDeleteAllScript(new GremlinSourceGraph());

        executeQuery(queryList);
    }

    @Override
    public void deleteAll(GremlinEntityType type) {
        final GremlinSource source = type.createGremlinSource();

        executeQuery(source.getGremlinScriptLiteral().generateDeleteAllScript(source));
    }

    @Override
    public <T> void deleteAll(@NonNull Class<T> domainClass) {
        final GremlinSource source = GremlinUtils.toGremlinSource(domainClass);

        executeQuery(source.getGremlinScriptLiteral().generateDeleteAllByClassScript(source));
    }

    private <T> T insertInternal(@NonNull T object, @NonNull GremlinSource source) {
        this.mappingConverter.write(object, source);

        executeQuery(source.getGremlinScriptLiteral().generateInsertScript(source));

        return object;
    }

    @Override
    public <T> T insert(@NonNull T object) {
        final GremlinSource source = GremlinUtils.toGremlinSource(object.getClass());

        return insertInternal(object, source);
    }

    @Override
    public <T> T findVertexById(@NonNull Object id, @NonNull Class<T> domainClass) {
        final GremlinSource<T> source = GremlinUtils.toGremlinSource(domainClass);

        if (source instanceof GremlinSourceVertex) {
            source.setId(id);
            return this.findByIdInternal(source);
        }

        throw new GremlinUnexpectedEntityTypeException("should be vertex domain for findVertexById");
    }

    private Object getEdgeAnnotatedFieldValue(@NonNull Field field, @NonNull Object vertexId) {
        if (field.getType() == String.class || field.getType() == Long.class || field.getType() == Integer.class) {
            return vertexId;
        } else if (field.getType().isPrimitive()) {
            throw new GremlinUnexpectedEntityTypeException("only String/Long/Integer type of Id Field is allowed");
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
        final GremlinSource source = GremlinUtils.toGremlinSource(domainClass);

        if (source instanceof GremlinSourceEdge) {
            return this.findById(id, domainClass);
        }

        throw new GremlinUnexpectedEntityTypeException("should be edge domain for findEdge");
    }

    private <T> T findByIdInternal(@NonNull GremlinSource<T> source) {
        final List<String> queryList = source.getGremlinScriptLiteral().generateFindByIdScript(source);
        final List<Result> results = this.executeQuery(queryList);

        if (results.isEmpty()) {
            return null;
        }

        Assert.isTrue(results.size() == 1, "should be only one domain with given id");

        return recoverDomain(source, results.get(0));
    }

    @Override
    public <T> T findById(@NonNull Object id, @NonNull Class<T> domainClass) {
        final GremlinSource<T> source = GremlinUtils.toGremlinSource(domainClass);

        if (source instanceof GremlinSourceGraph) {
            throw new UnsupportedOperationException("Gremlin graph cannot be findById.");
        }

        source.setId(id);

        return findByIdInternal(source);
    }

    private <T> T updateInternal(@NonNull T object, @NonNull GremlinSource source) {
        this.mappingConverter.write(object, source);

        final List<String> queryList = source.getGremlinScriptLiteral().generateUpdateScript(source);

        executeQuery(queryList);

        return object;
    }

    @Override
    public <T> T update(@NonNull T object) {
        final GremlinSource source = GremlinUtils.toGremlinSource(object);

        if (!(source instanceof GremlinSourceGraph) && notExistsById(source.getId(), object.getClass())) {
            throw new GremlinQueryException("cannot update the object doesn't exist");
        }

        return this.updateInternal(object, source);
    }

    @Override
    public <T> T save(@NonNull T object) {
        final GremlinSource source = GremlinUtils.toGremlinSource(object);

        if (source instanceof GremlinSourceGraph && isEmptyGraph(object)) {
            return insertInternal(object, source);
        } else if (!(source instanceof GremlinSourceGraph) && notExistsById(source.getId(), object.getClass())) {
            return insertInternal(object, source);
        } else {
            return updateInternal(object, source);
        }
    }

    @Override
    public <T> List<T> findAll(@NonNull Class<T> domainClass) {
        final GremlinSource<T> source = GremlinUtils.toGremlinSource(domainClass);

        if (source instanceof GremlinSourceGraph) {
            throw new UnsupportedOperationException("Gremlin graph cannot be findAll.");
        }

        final List<String> queryList = source.getGremlinScriptLiteral().generateFindAllScript(source);
        final List<Result> results = executeQuery(queryList);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        return recoverDomainList(source, results);
    }

    @Override
    public <T> void deleteById(@NonNull Object id, @NonNull Class<T> domainClass) {
        final GremlinSource source = GremlinUtils.toGremlinSource(domainClass);

        source.setId(id);

        final List<String> queryList = source.getGremlinScriptLiteral().generateDeleteByIdScript(source);

        executeQuery(queryList);
    }

    @Override
    public <T> boolean isEmptyGraph(@NonNull T object) {
        final GremlinSource source = GremlinUtils.toGremlinSource(object.getClass());

        if (source instanceof GremlinSourceGraph) {
            final GremlinScriptLiteralGraph literalGraph = (GremlinScriptLiteralGraph) source.getGremlinScriptLiteral();
            final List<String> queryList = literalGraph.generateIsEmptyScript();
            final List<Result> results = this.executeQuery(queryList);

            return results.isEmpty();
        }

        throw new GremlinQueryException("only graph domain is allowed.");
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

    private <T> T recoverDomain(@NonNull GremlinSource<T> source, @NonNull Result result) {
        final T domain;
        final Class<T> domainClass = source.getDomainClass();

        source.doGremlinResultRead(result);
        domain = this.mappingConverter.read(domainClass, source);

        if (source instanceof GremlinSourceEdge) {
            this.completeEdge(domain, (GremlinSourceEdge) source);
        }

        return domain;
    }

    private <T> List<T> recoverDomainList(@NonNull GremlinSource<T> source, @NonNull List<Result> results) {
        final List<T> domains = new ArrayList<>();

        results.forEach(r -> domains.add(recoverDomain(source, r)));

        return domains;
    }

    private <T> boolean notExistsById(@NonNull Object id, @NonNull Class<T> domainClass) {
        return !existsById(id, domainClass);
    }

    @Override
    public <T> boolean existsById(@NonNull Object id, @NonNull Class<T> domainClass) {
        return findById(id, domainClass) != null;
    }

    @Override
    public <T> List<T> find(@NonNull GremlinQuery query, @NonNull Class<T> domainClass) {
        final GremlinSource<T> source = GremlinUtils.toGremlinSource(domainClass);
        final QueryScriptGenerator generator = new QueryFindScriptGenerator(source);
        final List<String> queryList = generator.generate(query);
        final List<Result> results = this.executeQuery(queryList);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        return this.recoverDomainList(source, results);
    }
}

