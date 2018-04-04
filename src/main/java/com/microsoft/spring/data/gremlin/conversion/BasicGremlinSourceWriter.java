/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;


public class BasicGremlinSourceWriter {

    @Setter
    @Getter
    private Field entityId;

    @Setter
    @Getter
    private String entityLabel;

    public BasicGremlinSourceWriter(@NonNull Field id, @NonNull String label) {
        this.setEntityId(id);
        this.setEntityLabel(label);
    }

    String getEntityIdValue(@NonNull Object domain, @NonNull MappingGremlinConverter converter) {
        final Object id = converter.getFieldValue(domain, this.getEntityId());

        return id.toString();
    }
}

