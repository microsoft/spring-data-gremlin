/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.*;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationEdgeUnitTest {

    @Test
    public void testAnnotationEdgeDefaultLabel() {
        Assert.assertTrue(new GremlinEntityInformation<>(Dependency.class).isEntityEdge());
        Assert.assertNotNull(new GremlinEntityInformation<>(Dependency.class).getEntityLabel());
        Assert.assertEquals(new GremlinEntityInformation<>(Dependency.class).getEntityLabel(),
                Dependency.class.getSimpleName());
    }

    @Test
    public void testAnnotationEdgeSpecifiedLabel() {
        Assert.assertNotNull(new GremlinEntityInformation<>(Relationship.class).getEntityLabel());
        Assert.assertTrue(new GremlinEntityInformation<>(Relationship.class).isEntityEdge());
        Assert.assertEquals(new GremlinEntityInformation<>(Relationship.class).getEntityLabel(),
                TestConstants.EDGE_RELATIONSHIP_LABEL);
    }
}
