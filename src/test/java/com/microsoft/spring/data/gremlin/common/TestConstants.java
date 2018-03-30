/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

public class TestConstants {
    private TestConstants() {
        // Hide default constructor
    }

    public static final int DEFAULT_ENDPOINT_PORT = 443;
    public static final String FAKE_ENDPOINT = "XXX-xxx.XXX-xxx.cosmosdb.azure.com";
    public static final String FAKE_USERNAME = "XXX-xxx.username";
    public static final String FAKE_PASSWORD = "XXX-xxx.password";
    public static final String EMPTY_STRING = "";

    public static final String VERTEX_PERSON_LABEL = "label-person";
    public static final String VERTEX_PROJECT_LABEL = "label-project";
    public static final String EDGE_RELATIONSHIP_LABEL = "label-relationship";
}
