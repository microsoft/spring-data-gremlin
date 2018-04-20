/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;

import org.springframework.dao.DataAccessResourceFailureException;

public class GremlinQueryException extends DataAccessResourceFailureException {

    public GremlinQueryException(String msg) {
        super(msg);
    }

    public GremlinQueryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
