/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.config;

import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.common.GremlinPropertiesConfiguration;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource(value = {"classpath:application.properties"})
@EnableConfigurationProperties(GremlinPropertiesConfiguration.class)
public class AbstractGremlinConfigurationIT {

    private TestConfig testConfig;

    @Autowired
    private GremlinPropertiesConfiguration config;

    @Before
    public void setup() {
        this.testConfig = new TestConfig(this.config);
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
        Assert.assertNotNull(this.testConfig.gremlinTemplate());
    }

    private class TestConfig extends AbstractGremlinConfiguration {

        private GremlinPropertiesConfiguration config;

        public TestConfig(@NonNull GremlinPropertiesConfiguration config) {
            this.config = config;
        }

        @Override
        public GremlinFactory gremlinFactory() {
            return new GremlinFactory(this.config.getEndpoint(), this.config.getPort(), this.config.getUsername(),
                    this.config.getPassword());
        }
    }
}

