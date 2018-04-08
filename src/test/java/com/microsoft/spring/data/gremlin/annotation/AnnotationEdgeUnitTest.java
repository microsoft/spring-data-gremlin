/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.*;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Test;
import org.springframework.util.Assert;

public class AnnotationEdgeUnitTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testAnnotationEdgeDefaultLabel() {
        final Library libc = new Library(TestConstants.LIBC_ID, TestConstants.LIBC_NAME);
        final Library libm = new Library(TestConstants.LIBM_ID, TestConstants.LIBM_NAME);
        final Dependency edge = new Dependency(TestConstants.DEPENDENCY_ID, TestConstants.DEPENDENCY_NAME, libc, libm);

        final GremlinEntityInformation info = new GremlinEntityInformation(edge.getClass());

        Assert.notNull(info.getEntityLabel(), "entity label should not be null");
        Assert.isTrue(info.getEntityLabel().equals(edge.getClass().getSimpleName()), "should be default label");
        Assert.isTrue(info.isEntityEdge(), "should be edge of gremlin");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAnnotationEdgeSpecifiedLabel() {
        final Person person = new Person(TestConstants.VERTEX_PERSON_ID, TestConstants.VERTEX_PERSON_NAME);
        final Project project = new Project(TestConstants.VERTEX_PROJECT_ID, TestConstants.VERTEX_PROJECT_NAME,
                TestConstants.VERTEX_PROJECT_URI);
        final Relationship edge = new Relationship(TestConstants.EDGE_RELATIONSHIP_ID,
                TestConstants.EDGE_RELATIONSHIP_NAME, TestConstants.EDGE_RELATIONSHIP_LOCATION, person, project);

        final GremlinEntityInformation info = new GremlinEntityInformation(edge.getClass());

        Assert.notNull(info.getEntityLabel(), "entity label should not be null");
        Assert.isTrue(info.getEntityLabel().equals(TestConstants.EDGE_RELATIONSHIP_LABEL), "should be default label");
        Assert.isTrue(info.isEntityEdge(), "should be edge of gremlin");
    }

}
