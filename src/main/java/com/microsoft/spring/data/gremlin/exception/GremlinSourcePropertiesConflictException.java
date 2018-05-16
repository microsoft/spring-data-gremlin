/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;

import org.springframework.dao.TypeMismatchDataAccessException;

public class GremlinSourcePropertiesConflictException extends TypeMismatchDataAccessException {

    public GremlinSourcePropertiesConflictException(String msg) {
        super(msg);
    }

    public GremlinSourcePropertiesConflictException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
