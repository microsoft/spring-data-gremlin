/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.support;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.Network;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.common.domain.Relationship;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinInvalidEntityIdFieldException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class GremlinEntityInformationUnitTest {

    @Test
    public void testVertexEntityInformation() {
        final Person person = new Person(TestConstants.VERTEX_PERSON_ID, TestConstants.VERTEX_PERSON_NAME);
        final GremlinEntityInformation<Person, String> personInfo = new GremlinEntityInformation<>(Person.class);

        Assert.assertNotNull(personInfo.getIdField());
        Assert.assertEquals(personInfo.getId(person), TestConstants.VERTEX_PERSON_ID);
        Assert.assertEquals(personInfo.getIdType(), String.class);
        Assert.assertTrue(personInfo.createGremlinSource() instanceof GremlinSourceVertex);
    }

    @Test
    public void testEdgeEntityInformation() {
        final GremlinEntityInformation relationshipInfo =
                new GremlinEntityInformation<Relationship, String>(Relationship.class);

        Assert.assertNotNull(relationshipInfo.getIdField());
        Assert.assertTrue(relationshipInfo.createGremlinSource() instanceof GremlinSourceEdge);
    }

    @Test
    public void testGraphEntityInformation() {
        final GremlinEntityInformation networkInfo = new GremlinEntityInformation<Network, String>(Network.class);

        Assert.assertNotNull(networkInfo.getIdField());
        Assert.assertTrue(networkInfo.createGremlinSource() instanceof GremlinSourceGraph);
    }

    @Test(expected = GremlinUnexpectedEntityTypeException.class)
    public void testEntityInformationException() {
        new GremlinEntityInformation<TestDomain, String>(TestDomain.class).createGremlinSource();
    }

    @Test(expected = GremlinInvalidEntityIdFieldException.class)
    public void testEntityInformationNoIdException() {
        new GremlinEntityInformation<TestNoIdDomain, String>(TestNoIdDomain.class);
    }

    @Test(expected = GremlinInvalidEntityIdFieldException.class)
    public void testEntityInformationMultipleIdException() {
        new GremlinEntityInformation<TestMultipleIdDomain, String>(TestMultipleIdDomain.class);
    }

    @Test(expected = GremlinInvalidEntityIdFieldException.class)
    public void testEntityInformationNoStringIdException() {
        new GremlinEntityInformation<TestNoStringIdDomain, String>(TestNoStringIdDomain.class);
    }

    @Test(expected = GremlinInvalidEntityIdFieldException.class)
    public void testEntityInformationIdFieldAndIdAnnotation() {
        new GremlinEntityInformation<TestIdFieldAndIdAnnotation, String>(TestIdFieldAndIdAnnotation.class);
    }

    @Data
    private class TestDomain {
        private String id;
    }

    @Data
    private class TestNoIdDomain {
        private String name;
    }

    @Data
    private class TestMultipleIdDomain {
        @Id
        private String name;

        @Id
        private String location;
    }

    @Data
    private class TestIdFieldAndIdAnnotation {
        @Id
        private String name;

        @Id
        private String where;
    }

    @Data
    private class TestNoStringIdDomain {
        @Id
        private Date date;
    }
}
