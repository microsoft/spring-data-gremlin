/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.criteria;

import static com.microsoft.spring.data.gremlin.common.Constants.GREMLIN_PRIMITIVE_AND;
import static com.microsoft.spring.data.gremlin.common.Constants.GREMLIN_PRIMITIVE_OR;

public enum CriteriaType {
    IS_EQUAL,
    OR,
    AND,
    AFTER;

    public static String criteriaTypeToGremlin(CriteriaType type) {
        switch (type) {
            case OR:
                return GREMLIN_PRIMITIVE_OR;
            case AND:
                return GREMLIN_PRIMITIVE_AND;
            default:
                throw new UnsupportedOperationException("Unsupported criteria type.");
        }
    }
}
