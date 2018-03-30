/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.support;

import com.microsoft.spring.data.gremlin.query.GremlinOperations;
import com.microsoft.spring.data.gremlin.repository.GremlinRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class SimpleGremlinRepository<T, ID extends Serializable> implements GremlinRepository<T, ID> {

    private final GremlinOperations operations;
    private final GremlinEntityInformation<T, ID> information;

    public SimpleGremlinRepository(GremlinEntityInformation<T, ID> information, @NonNull ApplicationContext context) {
        this(information, context.getBean(GremlinOperations.class));
    }

    public SimpleGremlinRepository(GremlinEntityInformation<T, ID> information, @NonNull GremlinOperations operations) {
        this.information = information;
        this.operations = operations;
    }

    @Override
    public <S extends T> S save(@NonNull S domain) {
        this.operations.insert(domain);

        return domain;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(@NonNull Iterable<S> domains) {
        for (final S domain : domains) {
            this.save(domain);
        }

        return domains;
    }

    @Override
    public Iterable<T> findAll() {
        throw new NotImplementedException("findAll of Repository is not implemented yet");
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        throw new NotImplementedException("findAllById of Repository is not implemented yet");
    }

    @Override
    public Optional<T> findById(@NonNull ID id) {
        throw new NotImplementedException("findById of Repository is not implemented yet");
    }

    @Override
    public long count() {
        throw new NotImplementedException("count of Repository is not implemented yet");
    }

    @Override
    public void delete(T domain) {
        throw new NotImplementedException("delete of Repository is not implemented yet");
    }

    @Override
    public void deleteById(ID id) {
        throw new NotImplementedException("deleteById of Repository is not implemented yet");
    }

    @Override
    public void deleteAll() {
        this.operations.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends T> domains) {
        throw new NotImplementedException("deleteAll of Repository is not implemented yet");
    }

    @Override
    public boolean existsById(ID id) {
        throw new NotImplementedException("existsById of Repository is not implemented yet");
    }
}

