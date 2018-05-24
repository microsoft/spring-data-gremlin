/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("classpath:application.properties")
@EnableConfigurationProperties(GremlinConfiguration.class)
public class GremlinConfigurationUnitTest {

    @Autowired
    private GremlinConfiguration configuration;

    @Test
    public void testPropertiesConfiguration() {
        Assert.assertNotNull(this.configuration);

        Assert.assertNotNull(this.configuration.getEndpoint());
        Assert.assertNotNull(this.configuration.getPort());
        Assert.assertNotNull(this.configuration.getUsername());
        Assert.assertNotNull(this.configuration.getPassword());
    }
}
