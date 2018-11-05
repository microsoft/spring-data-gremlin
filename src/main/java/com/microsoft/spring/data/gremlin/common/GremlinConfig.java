/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.tinkerpop.gremlin.driver.ser.SerTokens;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;

@Getter
@Setter
@Builder(builderMethodName = "defaultBuilder")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class GremlinConfig {
    private String endpoint;

    private int port;

    private String username;

    private String password;

    private boolean sslEnabled;

    private boolean telemetryAllowed;

    private String serializer;

    public static GremlinConfigBuilder builder(String endpoint, String username, String password) {
        return defaultBuilder()
                .endpoint(endpoint)
                .username(username)
                .password(password)
                .port(Constants.DEFAULT_ENDPOINT_PORT)
                .sslEnabled(true)
                .serializer(Serializers.GRAPHSON.toString())
                .telemetryAllowed(true);
    }
}
