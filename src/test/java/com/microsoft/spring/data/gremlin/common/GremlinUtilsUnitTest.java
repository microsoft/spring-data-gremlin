/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import com.microsoft.spring.data.gremlin.conversion.source.AbstractGremlinSource;
import org.junit.Test;

public class GremlinUtilsUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateIntegerInstance() {
        GremlinUtils.createInstance(Integer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTestConstantsInstance() {
        GremlinUtils.createInstance(TestConstants.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAbstractInstance() {
        GremlinUtils.createInstance(AbstractGremlinSource.class);
    }
}
