/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.criteria;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.spring.data.gremlin.query.criteria.CriteriaType.*;

public class CriteriaUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnaryInstanceException() {
        final List<Object> values = new ArrayList<>();

        Criteria.getUnaryInstance(OR, "fake-name", values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBinaryInstanceException() {
        final List<Object> values = new ArrayList<>();
        final Criteria left = Criteria.getUnaryInstance(IS_EQUAL, "fake-name", values);
        final Criteria right = Criteria.getUnaryInstance(IS_EQUAL, "fake-name", values);

        Criteria.getBinaryInstance(IS_EQUAL, left, right);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCriteriaTypeToGremlinException() {
        CriteriaType.criteriaTypeToGremlin(IS_EQUAL);
    }

    @Test
    public void testCriteriaOperationType() {
        Assert.assertTrue(Criteria.isBinaryOperation(AND));
        Assert.assertFalse(Criteria.isBinaryOperation(AFTER));

        Assert.assertTrue(Criteria.isUnaryOperation(AFTER));
        Assert.assertFalse(Criteria.isUnaryOperation(OR));
    }
}
