/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import org.springframework.lang.NonNull;

import java.lang.reflect.Field;

public class GremlinSourceVertex extends BasicGremlinSource {

    public GremlinSourceVertex() {
        super();
    }

    public GremlinSourceVertex(@NonNull String id) {
        super();
        super.setId(id);
    }

    public GremlinSourceVertex(@NonNull Field idField, @NonNull String label) {
        super();
        super.setIdField(idField);
        super.setLabel(label);
    }
}
