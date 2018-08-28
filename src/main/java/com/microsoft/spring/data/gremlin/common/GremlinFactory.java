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
import org.apache.tinkerpop.gremlin.util.Gremlin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;


public class GremlinFactory {

    @Getter
    private Cluster gremlinCluster;

    private GremlinConfig gremlinConfig;

    @Autowired(required = false)
    private TelemetryTracker telemetryTracker;

    public GremlinFactory(@NonNull GremlinConfig gremlinConfig) {
        final int port = gremlinConfig.getPort();
        if (port <= 0 || port > 65535) {
            gremlinConfig.setPort(Constants.DEFAULT_ENDPOINT_PORT);
        }

        this.gremlinConfig = gremlinConfig;
    }

    private void trackTelemetryCustomEvent() {
        final Map<String, String> customProperties = new HashMap<>();

        customProperties.put(TelemetryProperties.PROPERTY_SERVICE_NAME, "gremlin");

        TelemetryUtils.telemetryTriggerEvent(telemetryTracker, getClass().getSimpleName(), customProperties);
    }

    private Cluster createGremlinCluster() throws GremlinIllegalConfigurationException {
        final Cluster cluster;

        try {
            cluster = Cluster.build(this.gremlinConfig.getEndpoint())
                    .serializer(Serializers.DEFAULT_RESULT_SERIALIZER)
                    .credentials(this.gremlinConfig.getUsername(), this.gremlinConfig.getPassword())
                    .enableSsl(true)
                    .port(this.gremlinConfig.getPort())
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
