/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;

import org.springframework.lang.Nullable;

public class IllegalGremlinConfigurationException extends IllegalArgumentException {

    public IllegalGremlinConfigurationException(String msg) {
        super(msg);
    }

    public IllegalGremlinConfigurationException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
