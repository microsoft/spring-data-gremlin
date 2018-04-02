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
    public static final String DEFAULT_REPOSITORY_IMPLEMENT_POSTFIX = "Impl";
    public static final String DEFAULT_NAMED_QUERIES_LOCATION = "";

    public static final String GREMLIN_MODULE_NAME = "Gremlin";
    public static final String GREMLIN_MODULE_PREFIX = "gremlin";
    public static final String GREMLIN_MAPPING_CONTEXT = "gremlinMappingContext";

    public static final String GREMLIN_PRIMITIVE_GRAPH = "g";
    public static final String GREMLIN_PRIMITIVE_INVOKE = ".";
    public static final String GREMLIN_PRIMITIVE_DROP = "drop()";

    public static final String GREMLIN_PRIMITIVE_EDGE_ALL = "E()";
    public static final String GREMLIN_PRIMITIVE_ADD_EDGE = "addE('%s')";

    public static final String GREMLIN_PRIMITIVE_VERTEX = "V('%s')";
    public static final String GREMLIN_PRIMITIVE_VERTEX_ALL = "V()";
    public static final String GREMLIN_PRIMITIVE_ADD_VERTEX = "addV('%s')";
    public static final String GREMLIN_PRIMITIVE_TO_VERTEX = "to(g.V('%s'))";

    public static final String GREMLIN_PRIMITIVE_PROPERTY_STRING = "property('%s', '%s')";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_NUMBER = "property('%s', %d)";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_BOOLEAN = "property('%s', %b)";

    public static final String GREMLIN_SCRIPT_HEAD = GREMLIN_PRIMITIVE_GRAPH + GREMLIN_PRIMITIVE_INVOKE;

    public static final String GREMLIN_SCRIPT_EDGE_DROP = String.join(GREMLIN_PRIMITIVE_INVOKE,
            GREMLIN_PRIMITIVE_GRAPH,
            GREMLIN_PRIMITIVE_EDGE_ALL,
            GREMLIN_PRIMITIVE_DROP
    );

    public static final String GREMLIN_SCRIPT_VERTEX_DROP = String.join(GREMLIN_PRIMITIVE_INVOKE,
            GREMLIN_PRIMITIVE_GRAPH,
            GREMLIN_PRIMITIVE_VERTEX_ALL,
            GREMLIN_PRIMITIVE_DROP
    );
}
