/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.common.domain.Network;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.common.domain.Relationship;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Assert;
import org.junit.Test;

public class GremlinSourceSimpleFactoryUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSimpleFactoryInstance() {
        GremlinUtils.createInstance(GremlinSourceSimpleFactory.class);
    }

    @Test(expected = GremlinUnexpectedEntityTypeException.class)
    public void testUnexpectedGremlinEntityType() {
        final GremlinEntityInformation<Person, String> info = new GremlinEntityInformation<>(Person.class);
        GremlinSourceSimpleFactory.createGremlinSource(info.getIdField(), info.getEntityLabel(),
                GremlinEntityType.UNKNOWN);
    }

    @Test
    public void testCreateGremlinSourceVertex() {
        final GremlinEntityInformation<Person, String> info = new GremlinEntityInformation<>(Person.class);
        final GremlinSource source = GremlinSourceSimpleFactory.createGremlinSource(info.getIdField(),
                info.getEntityLabel(), info.getEntityType());

        Assert.assertTrue(source instanceof GremlinSourceVertex);
        Assert.assertEquals(source.getLabel(), info.getEntityLabel());
    }

    @Test
    public void testCreateGremlinSourceEdge() {
        final GremlinEntityInformation<Relationship, String> info = new GremlinEntityInformation<>(Relationship.class);
        final GremlinSource source = GremlinSourceSimpleFactory.createGremlinSource(info.getIdField(),
                info.getEntityLabel(), info.getEntityType());

        Assert.assertTrue(source instanceof GremlinSourceEdge);
        Assert.assertEquals(source.getLabel(), info.getEntityLabel());
    }

    @Test
    public void testCreateGremlinSourceGraph() {
        final GremlinEntityInformation<Network, String> info = new GremlinEntityInformation<>(Network.class);
        final GremlinSource source = GremlinSourceSimpleFactory.createGremlinSource(info.getIdField(),
                info.getEntityLabel(), info.getEntityType());

        Assert.assertTrue(source instanceof GremlinSourceGraph);
    }
}
