/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.support;

import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.Network;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.common.domain.Relationship;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class GremlinEntityInformationUnitTest {

    @Test
    public void testVertexEntityInformation() {
        final Person person = new Person(TestConstants.VERTEX_PERSON_ID, TestConstants.VERTEX_PERSON_NAME);
        final GremlinEntityInformation<Person, String> personInfo = new GremlinEntityInformation(Person.class);

        Assert.assertNotNull(personInfo.getIdField());
        Assert.assertEquals(personInfo.getId(person), TestConstants.VERTEX_PERSON_ID);
        Assert.assertEquals(personInfo.getIdType(), String.class);
        Assert.assertEquals(personInfo.getEntityLabel(), TestConstants.VERTEX_PERSON_LABEL);
        Assert.assertEquals(personInfo.getEntityType(), GremlinEntityType.VERTEX);
        Assert.assertTrue(personInfo.getGremlinSource() instanceof GremlinSourceVertex);
    }

    @Test
    public void testEdgeEntityInformation() {
        final GremlinEntityInformation relationshipInfo =
                new GremlinEntityInformation<Relationship, String>(Relationship.class);

        Assert.assertNotNull(relationshipInfo.getIdField());
        Assert.assertEquals(relationshipInfo.getEntityLabel(), TestConstants.EDGE_RELATIONSHIP_LABEL);
        Assert.assertEquals(relationshipInfo.getEntityType(), GremlinEntityType.EDGE);
        Assert.assertTrue(relationshipInfo.getGremlinSource() instanceof GremlinSourceEdge);
    }

    @Test
    public void testGraphEntityInformation() {
        final GremlinEntityInformation networkInfo = new GremlinEntityInformation<Network, String>(Network.class);

        Assert.assertNotNull(networkInfo.getIdField());
        Assert.assertNull(networkInfo.getEntityLabel());
        Assert.assertEquals(networkInfo.getEntityType(), GremlinEntityType.GRAPH);
        Assert.assertTrue(networkInfo.getGremlinSource() instanceof GremlinSourceGraph);
    }

    @Test(expected = GremlinUnexpectedEntityTypeException.class)
    public void testEntityInformationException() {
        new GremlinEntityInformation<TestEntity, String>(TestEntity.class);
    }

    @Data
    private class TestEntity {
        private String id;
    }
}
