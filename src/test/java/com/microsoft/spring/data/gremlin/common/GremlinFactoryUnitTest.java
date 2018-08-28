/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import com.microsoft.spring.data.gremlin.exception.GremlinIllegalConfigurationException;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.junit.Assert;
import org.junit.Test;

public class GremlinFactoryUnitTest {

    @Test(expected = GremlinIllegalConfigurationException.class)
    public void testGremlinFactoryException() {
        final GremlinConfig config = GremlinConfig.builder(TestConstants.FAKE_ENDPOINT, TestConstants.FAKE_USERNAME,
                TestConstants.FAKE_PASSWORD).build();
        final GremlinFactory factory = new GremlinFactory(config);

        factory.getGremlinClient();
    }

    @Test
    public void testGremlinFactoryNormal() {
        final Client client;
        final GremlinFactory factory;

        final GremlinConfig config = GremlinConfig.builder(TestConstants.EMPTY_STRING, TestConstants.EMPTY_STRING,
                TestConstants.EMPTY_STRING).port(TestConstants.ILLEGAL_ENDPOINT_PORT).build();
        factory = new GremlinFactory(config);
        client = factory.getGremlinClient();

        Assert.assertEquals(client.getCluster().getPort(), TestConstants.DEFAULT_ENDPOINT_PORT);
        Assert.assertFalse(client.getSettings().getSession().isPresent());
    }
}
