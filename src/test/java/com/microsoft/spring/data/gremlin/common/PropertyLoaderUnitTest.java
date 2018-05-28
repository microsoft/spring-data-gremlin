/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import org.junit.Assert;
import org.junit.Test;

public class PropertyLoaderUnitTest {

    @Test
    public void testGetProjectVersion() {
        final String version = PropertyLoader.getProjectVersion();

        Assert.assertNotNull(version);
        Assert.assertNotEquals(version, "");
    }

    @Test
    public void testGetApplicationTelemetryAllowed() {
        final boolean isAllowed = PropertyLoader.isApplicationTelemetryAllowed();

        Assert.assertNotNull(isAllowed);
        Assert.assertTrue(isAllowed);
    }
}
