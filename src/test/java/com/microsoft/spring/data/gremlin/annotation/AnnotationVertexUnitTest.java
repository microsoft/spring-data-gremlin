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

public class AnnotationVertexUnitTest {

    @Test
    public void testAnnotationVertexDefaultLabel() {
        final Library libc = new Library(TestConstants.LIBC_ID, TestConstants.LIBC_NAME);
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation info = new GremlinEntityInformation(libc.getClass());

        Assert.assertNotNull(info.getEntityLabel());
        Assert.assertEquals(info.getEntityLabel(), libc.getClass().getSimpleName());
        Assert.assertTrue(info.isEntityVertex());
    }

    @Test
    public void testAnnotationVertexSpecifiedLabel() {
        final Person person = new Person(TestConstants.VERTEX_PERSON_ID, TestConstants.VERTEX_PERSON_NAME);
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation info = new GremlinEntityInformation(person.getClass());

        Assert.assertNotNull(info.getEntityLabel());
        Assert.assertEquals(info.getEntityLabel(), TestConstants.VERTEX_PERSON_LABEL);
        Assert.assertTrue(info.isEntityVertex());
    }
}
