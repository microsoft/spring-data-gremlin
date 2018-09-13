/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.config;

import com.microsoft.spring.data.gremlin.common.GremlinConfig;
import com.microsoft.spring.data.gremlin.common.TestGremlinProperties;
import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.telemetry.EmptyTracker;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryTracker;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource(value = {"classpath:application.properties"})
@EnableConfigurationProperties(TestGremlinProperties.class)
public class AbstractGremlinConfigurationIT {

    private TestConfig testConfig;

    @Autowired
    private TestGremlinProperties testProps;

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TestRepositoryConfiguration.class));

    @Before
    public void setup() {
        this.testConfig = new TestConfig(GremlinConfig.builder(testProps.getEndpoint(), testProps.getUsername(),
                testProps.getPassword()).build());
    }

    @Test
    public void testGremlinFactory() {
        Assert.assertNotNull(this.testConfig.gremlinFactory());
        Assert.assertNotNull(this.testConfig.gremlinFactory().getGremlinClient());
    }

    @Test
    @SneakyThrows
    public void testMappingGremlinConverter() {
        Assert.assertNotNull(this.testConfig.mappingGremlinConverter());
    }

    @Test
    @SneakyThrows
    public void testGremlinTemplate() {
        Assert.assertNotNull(this.testConfig.gremlinTemplate(testConfig.gremlinFactory()));
    }

    private class TestConfig extends AbstractGremlinConfiguration {

        private GremlinConfig gremlinConfig;

        public TestConfig(@NonNull GremlinConfig gremlinConfig) {
            this.gremlinConfig = gremlinConfig;
        }

        @Override
        public GremlinConfig getGremlinConfig() {
            return this.gremlinConfig;
        }
    }

    @Test
    public void testTelemetryTrackerBean() {
        this.contextRunner
                .withPropertyValues("gremlin.telemetry-allowed=true")
                .run(context -> Assert.assertNotNull(context.getBean(TelemetryTracker.class)));

        this.contextRunner
                .run(context -> Assert.assertNotNull(context.getBean(TelemetryTracker.class)));
    }

    @Test
    public void testDisabledTelemetry() {
        this.contextRunner
                .withPropertyValues("gremlin.telemetry-allowed=false")
                .run(context -> Assert.assertTrue(context.getBean(TelemetryTracker.class) instanceof EmptyTracker));
    }
}

