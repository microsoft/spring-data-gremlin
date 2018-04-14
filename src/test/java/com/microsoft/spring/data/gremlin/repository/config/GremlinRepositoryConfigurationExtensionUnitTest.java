/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.config;

import com.microsoft.spring.data.gremlin.repository.GremlinRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationSource;

public class GremlinRepositoryConfigurationExtensionUnitTest {

    private static final String GREMLIN_MODULE_NAME = "Gremlin";
    private static final String GREMLIN_MODULE_PREFIX = "gremlin";
    private static final String GREMLIN_MAPPING_CONTEXT = "gremlinMappingContext";

    private ResourceLoader loader;
    private Environment environment;
    private BeanDefinitionRegistry registry;
    private RepositoryConfigurationSource config;
    private GremlinRepositoryConfigurationExtension extension;
    private StandardAnnotationMetadata metadata;

    @Before
    public void setup() {
        this.extension = new GremlinRepositoryConfigurationExtension();
    }

    @Test
    public void testGremlinRepositoryConfigurationExtensionGetters() {
        Assert.assertEquals(this.extension.getModuleName(), GREMLIN_MODULE_NAME);
        Assert.assertEquals(this.extension.getModulePrefix(), GREMLIN_MODULE_PREFIX);
        Assert.assertEquals(this.extension.getIdentifyingTypes().size(), 1);

        Assert.assertSame(this.extension.getIdentifyingTypes().toArray()[0], GremlinRepository.class);
        Assert.assertTrue(this.extension.getIdentifyingAnnotations().isEmpty());
    }

    @Test
    public void testGremlinRepositoryConfigurationExtensionRegisterBeansForRoot() {
        this.loader = new PathMatchingResourcePatternResolver();
        this.environment = new StandardEnvironment();
        this.registry = new DefaultListableBeanFactory();
        this.metadata = new StandardAnnotationMetadata(GremlinConfig.class, true);

        this.config = new AnnotationRepositoryConfigurationSource(this.metadata, EnableGremlinRepository.class,
                this.loader, this.environment, this.registry);

        Assert.assertFalse(this.registry.containsBeanDefinition(GREMLIN_MAPPING_CONTEXT));

        this.extension.registerBeansForRoot(this.registry, this.config);

        Assert.assertTrue(this.registry.containsBeanDefinition(GREMLIN_MAPPING_CONTEXT));
    }

    @EnableGremlinRepository
    private static class GremlinConfig {

    }
}
