/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

public class Constants {
    private Constants() {
        // Hide default constructor
    }

    public static final String PROPERTY_ID = "id";

    public static final String DEFAULT_VERTEX_LABEL = "";
    public static final String DEFAULT_EDGE_LABEL = "";
    public static final String DEFAULT_COLLECTION_NAME = "";
    public static final String DEFAULT_ENDPOINT_PORT = "443";

    public static final String GREMLIN_PRIMITIVE_GRAPH = "g";
    public static final String GREMLIN_PRIMITIVE_INVOKE = ".";
    public static final String GREMLIN_PRIMITIVE_ADD_VERTEX = "addV('%s')";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_STRING = "property('%s', '%s')";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_NUMBER = "property('%s', %d)";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_BOOLEAN = "property('%s', %b)";
}
