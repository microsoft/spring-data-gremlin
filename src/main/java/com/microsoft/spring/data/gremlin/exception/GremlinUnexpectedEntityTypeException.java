/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;

public class GremlinUnexpectedEntityTypeException extends GremlinEntityInformationException {

    public GremlinUnexpectedEntityTypeException(String msg) {
        super(msg);
    }

    public GremlinUnexpectedEntityTypeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
