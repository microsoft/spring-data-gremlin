/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.criteria;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CriteriaUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnaryInstanceException() {
        final List<Object> values = new ArrayList<>();

        Criteria.getUnaryInstance(CriteriaType.OR, "fake-name", values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBinaryInstanceException() {
        final List<Object> values = new ArrayList<>();
        final Criteria left = Criteria.getUnaryInstance(CriteriaType.IS_EQUAL, "fake-name", values);
        final Criteria right = Criteria.getUnaryInstance(CriteriaType.IS_EQUAL, "fake-name", values);

        Criteria.getBinaryInstance(CriteriaType.IS_EQUAL, left, right);
    }
}
