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
        Assert.assertNull(new GremlinEntityInformation<>(Network.class).getEntityLabel());
        Assert.assertTrue(new GremlinEntityInformation<>(Network.class).isEntityGraph());
    }

    @Test
    public void testAnnotationGraphSpecifiedCollection() {
        Assert.assertNull(new GremlinEntityInformation<>(Roadmap.class).getEntityLabel());
        Assert.assertTrue(new GremlinEntityInformation<>(Roadmap.class).isEntityGraph());
    }
}
