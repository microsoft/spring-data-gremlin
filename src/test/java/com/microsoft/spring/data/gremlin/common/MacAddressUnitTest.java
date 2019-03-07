/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import com.microsoft.spring.data.gremlin.telemetry.MacAddress;
import org.junit.Assert;
import org.junit.Test;

public class MacAddressUnitTest {

    @Test
    public void testGetHashMacNormal() {
        Assert.assertNotNull(MacAddress.getHashMac());
        Assert.assertFalse(MacAddress.getHashMac().isEmpty());
        Assert.assertFalse(MacAddress.isValidHashMacFormat(""));
        Assert.assertTrue(MacAddress.isValidHashMacFormat(MacAddress.getHashMac()));
    }
}
