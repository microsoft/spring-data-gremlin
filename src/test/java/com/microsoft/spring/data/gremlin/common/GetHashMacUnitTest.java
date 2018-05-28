/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import org.junit.Assert;
import org.junit.Test;

public class GetHashMacUnitTest {

    @Test
    public void testGetHashMacNormal() {
        Assert.assertNotNull(GetHashMac.getHashMac());
        Assert.assertFalse(GetHashMac.getHashMac().isEmpty());
        Assert.assertFalse(GetHashMac.isValidHashMacFormat(""));
        Assert.assertTrue(GetHashMac.isValidHashMacFormat(GetHashMac.getHashMac()));
    }
}
