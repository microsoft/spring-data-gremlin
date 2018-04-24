/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;

import org.springframework.dao.TypeMismatchDataAccessException;

public class GremlinUnexpectedSourceTypeException extends TypeMismatchDataAccessException {

    public GremlinUnexpectedSourceTypeException(String msg) {
        super(msg);
    }

    public GremlinUnexpectedSourceTypeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
