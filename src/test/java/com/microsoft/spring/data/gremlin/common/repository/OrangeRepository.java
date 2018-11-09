/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common.repository;

import com.microsoft.spring.data.gremlin.common.domain.Orange;
import com.microsoft.spring.data.gremlin.repository.GremlinRepository;

public interface OrangeRepository extends GremlinRepository<Orange, String> {
}
