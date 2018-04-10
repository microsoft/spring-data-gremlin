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
import com.microsoft.spring.data.gremlin.conversion.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.GremlinSourceVertex;
import org.junit.Assert;
import org.junit.Test;

public class GremlinEntityInformationUnitTest {

    @Test
    public void testVertexEntityInformation() {
        final GremlinEntityInformation personInfo = new GremlinEntityInformation<Person, String>(Person.class);

        Assert.assertNotNull(personInfo.getIdField());
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
}
