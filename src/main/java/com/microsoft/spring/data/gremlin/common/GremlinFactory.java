/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import com.microsoft.spring.data.gremlin.exception.GremlinIllegalConfigurationException;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryProperties;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryTracker;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryUtils;
import lombok.Getter;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;


public class GremlinFactory {

    @Getter
    private Cluster gremlinCluster;
    private String endpoint;
    private String port;
    private String username;
    private String password;

    @Autowired(required = false)
    private TelemetryTracker telemetryTracker;

    public GremlinFactory(@NonNull String endpoint, @Nullable String port,
                          @NonNull String username, @NonNull String password) {

        if (port == null || port.isEmpty()) {
            this.port = Constants.DEFAULT_ENDPOINT_PORT;
        } else {
            this.port = port;
        }

        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }

    private void trackTelemetryCustomEvent() {
        final Map<String, String> customProperties = new HashMap<>();

        customProperties.put(TelemetryProperties.PROPERTY_SERVICE_NAME, "gremlin");

        TelemetryUtils.telemetryTriggerEvent(telemetryTracker, getClass().getSimpleName(), customProperties);
    }

    private Cluster createGremlinCluster() throws GremlinIllegalConfigurationException {
        final int port;
        final Cluster cluster;

        try {
            port = Integer.parseInt(this.port);
            cluster = Cluster.build(this.endpoint)
                    .serializer(Serializers.DEFAULT_RESULT_SERIALIZER)
                    .credentials(this.username, this.password)
                    .enableSsl(true)
                    .port(port)
                    .create();
        } catch (IllegalArgumentException e) {
            throw new GremlinIllegalConfigurationException("Invalid configuration of Gremlin", e);
        }

        this.trackTelemetryCustomEvent();

        return cluster;
    }

    public Client getGremlinClient() {

        if (this.gremlinCluster == null) {
            this.gremlinCluster = this.createGremlinCluster();
        }

        return this.gremlinCluster.connect();
    }
}
