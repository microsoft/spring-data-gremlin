/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.query.GremlinOperations;
import org.springframework.lang.NonNull;

public interface GremlinQueryExecution {
    Object execute(GremlinQuery query, Class<?> type);

    final class FindExecution implements GremlinQueryExecution {

        private final GremlinOperations operations;

        public FindExecution(@NonNull GremlinOperations operations) {
            this.operations = operations;
        }

        @Override
        public Object execute(@NonNull GremlinQuery query, @NonNull Class<?> type) {
            return this.operations.find(query, type);
        }
    }
}
