/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import java.util.List;

public interface QueryScriptGenerator {

    <T> List<String> generate(GremlinQuery query, Class<T> domainClass);
}
