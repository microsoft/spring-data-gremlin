/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;

public class GremlinEntityInformationException extends IllegalStateException {

    public GremlinEntityInformationException(String msg) {
        super(msg);
    }

    public GremlinEntityInformationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
