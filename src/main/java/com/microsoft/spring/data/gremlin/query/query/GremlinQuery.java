/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.query.criteria.Criteria;
import lombok.Getter;
import lombok.NonNull;

public class GremlinQuery {

    @Getter
    private final Criteria criteria;

    public GremlinQuery(@NonNull Criteria criteria) {
        this.criteria = criteria;
    }
}
