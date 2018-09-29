/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.EntityConverter;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class MappingGremlinConverter
        implements EntityConverter<GremlinPersistentEntity<?>, GremlinPersistentProperty, Object, GremlinSource>,
        ApplicationContextAware {

    protected final MappingContext<? extends GremlinPersistentEntity<?>, GremlinPersistentProperty> mappingContext;
    protected GenericConversionService conversionService;
    private ApplicationContext applicationContext;

    public MappingGremlinConverter(
            MappingContext<? extends GremlinPersistentEntity<?>, GremlinPersistentProperty> context) {

        this.mappingContext = context;
        this.conversionService = new GenericConversionService();
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public MappingContext<? extends GremlinPersistentEntity<?>, GremlinPersistentProperty> getMappingContext() {
        return this.mappingContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override
    public <T extends Object> T read(Class<T> domainClass, @NonNull GremlinSource source) {
        @SuppressWarnings("unchecked") final GremlinSource<T> gremlinSource = (GremlinSource<T>) source;

        return gremlinSource.doGremlinSourceRead(domainClass, this);
    }

    @Override
    public void write(@NonNull Object domain, @NonNull GremlinSource source) {
        source.doGremlinSourceWrite(domain, this);
    }

    public ConvertingPropertyAccessor getPropertyAccessor(@NonNull Object domain) {
        final GremlinPersistentEntity<?> persistentEntity = this.getPersistentEntity(domain.getClass());
        Assert.notNull(persistentEntity, "persistentEntity should not be null");

        final PersistentPropertyAccessor accessor = persistentEntity.getPropertyAccessor(domain);

        return new ConvertingPropertyAccessor(accessor, this.conversionService);
    }

    public GremlinPersistentEntity<?> getPersistentEntity(@NonNull Class<?> domainClass) {
        return mappingContext.getPersistentEntity(domainClass);
    }

    private String getIdFieldName(@NonNull Object domain) {
        return GremlinUtils.getIdField(domain.getClass()).getName();
    }

    private Object getFieldValue(@NonNull Object domain, @NonNull String fieldName) {
        final ConvertingPropertyAccessor accessor = this.getPropertyAccessor(domain);
        final GremlinPersistentEntity<?> persistentEntity = this.getPersistentEntity(domain.getClass());
        final PersistentProperty property = persistentEntity.getPersistentProperty(fieldName);
        Assert.notNull(property, "persistence property should not be null");

        final Object value = accessor.getProperty(property);
        Assert.notNull(value, "PersistentProperty should not be null");

        return value;
    }

    public Object getIdFieldValue(@NonNull Object domain) {
        return this.getFieldValue(domain, this.getIdFieldName(domain));
    }
}

