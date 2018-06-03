/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.query.criteria.Criteria;
import lombok.*;
import org.springframework.util.Assert;

import java.util.List;

public class GremlinQuery {

    @Getter
    private final Criteria criteria;

    @Setter(AccessLevel.PRIVATE)
    private QueryScriptGenerator generator;

    public GremlinQuery(@NonNull Criteria criteria) {
        this.criteria = criteria;
    }

    public void setScriptGenerator(@NonNull QueryScriptGenerator generator) {
        this.setGenerator(generator);
    }

    public <T> List<String> doSentenceGenerate(@NonNull Class<T> domainClass) {
        Assert.notNull(this.generator, "Generator should not be null");

        return this.generator.generate(this, domainClass);
    }
}
