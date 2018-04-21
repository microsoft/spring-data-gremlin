/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.Library;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationVertexUnitTest {

    @Test
    public void testAnnotationVertexDefaultLabel() {
        Assert.assertNotNull(new GremlinEntityInformation<>(Library.class).getEntityLabel());
        Assert.assertTrue(new GremlinEntityInformation<>(Library.class).isEntityVertex());
        Assert.assertEquals(new GremlinEntityInformation<>(Library.class).getEntityLabel(),
                Library.class.getSimpleName());
    }

    @Test
    public void testAnnotationVertexSpecifiedLabel() {
        Assert.assertNotNull(new GremlinEntityInformation<>(Person.class).getEntityLabel());
        Assert.assertTrue(new GremlinEntityInformation<>(Person.class).isEntityVertex());
        Assert.assertEquals(new GremlinEntityInformation<>(Person.class).getEntityLabel(),
                TestConstants.VERTEX_PERSON_LABEL);
    }
}
