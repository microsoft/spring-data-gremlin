/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.springframework.lang.NonNull;

public class BasicGremlinSourceWriter {

    private GremlinEntityInformation entityInformation;

    @SuppressWarnings("unchecked")
    public BasicGremlinSourceWriter(@NonNull Class<?> domainClass) {
        this.entityInformation = new GremlinEntityInformation(domainClass);
    }

    String getPersistentEntityId() {
        return this.entityInformation.getIdField().toString();
    }

    private String getPersistentEntityLabel() {
        return this.entityInformation.getIdField().toString();
    }

    void setGremlinSourceReserved(@NonNull GremlinSource source) {

        source.setId(this.getPersistentEntityId());
        source.setLabel(this.getPersistentEntityLabel());
    }
}

