/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.mapping;

import org.springframework.data.mapping.PersistentEntity;

public interface GremlinPersistentEntity<T> extends PersistentEntity<T, GremlinPersistentProperty> {

}
