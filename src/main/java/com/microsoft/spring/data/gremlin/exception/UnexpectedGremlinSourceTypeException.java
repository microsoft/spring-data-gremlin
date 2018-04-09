/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;

import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.lang.Nullable;

public class UnexpectedGremlinSourceTypeException extends TypeMismatchDataAccessException {

    public UnexpectedGremlinSourceTypeException(String msg) {
        super(msg);
    }

    public UnexpectedGremlinSourceTypeException(@Nullable  String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
