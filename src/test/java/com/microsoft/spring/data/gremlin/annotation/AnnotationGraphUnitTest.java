/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import com.microsoft.spring.data.gremlin.common.domain.Network;
import com.microsoft.spring.data.gremlin.common.domain.Roadmap;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationGraphUnitTest {

    @Test
    public void testAnnotationGraphDefaultCollection() {
        final Network network = new Network();
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation info = new GremlinEntityInformation(network.getClass());

        Assert.assertNull(info.getEntityLabel());
        Assert.assertTrue(info.isEntityGraph());
    }

    @Test
    public void testAnnotationGraphSpecifiedCollection() {
        final Roadmap roadmap = new Roadmap();
        @SuppressWarnings("unchecked")
        final GremlinEntityInformation info = new GremlinEntityInformation(roadmap.getClass());

        Assert.assertNull(info.getEntityLabel());
        Assert.assertTrue(info.isEntityGraph());
    }
}
