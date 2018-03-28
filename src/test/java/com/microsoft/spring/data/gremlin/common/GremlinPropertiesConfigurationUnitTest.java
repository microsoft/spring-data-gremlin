/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("classpath:application.properties")
@EnableConfigurationProperties(GremlinPropertiesConfiguration.class)
public class GremlinPropertiesConfigurationUnitTest {

    @Autowired
    private GremlinPropertiesConfiguration configuration;

    @Test
    public void testPropertiesConfiguration() {
        Assert.notNull(this.configuration, "Properties configuration should not be null");

        Assert.notNull(this.configuration.getEndpoint(), "Properties endpoint should not be null");
        Assert.notNull(this.configuration.getPort(), "Properties port should not be null");
        Assert.notNull(this.configuration.getUsername(), "Properties username should not be null");
        Assert.notNull(this.configuration.getPassword(), "Properties password should not be null");
    }
}
