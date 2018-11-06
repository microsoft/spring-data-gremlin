/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tinkerpop.gremlin.driver.ser.SerTokens;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("gremlin")
public class TestGremlinProperties {
    private String endpoint;

    private int port;

    private String username;

    private String password;

    private boolean sslEnabled = true;

    private boolean telemetryAllowed = true;

    private String serializer = Serializers.GRAPHSON.toString();
}
