/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.telemetry;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

import static com.microsoft.spring.data.gremlin.telemetry.TelemetryProperties.PROPERTY_INSTALLATION_ID;
import static com.microsoft.spring.data.gremlin.telemetry.TelemetryProperties.PROPERTY_VERSION;

public class TelemetryTracker {

    private static final String PROJECT_VERSION = TelemetryTracker.class.getPackage().getImplementationVersion();

    private static final String PROJECT_INFO = "spring-data-gremlin" + "/" + PROJECT_VERSION;

    private TelemetryClient client;

    public TelemetryTracker() {
        this.client = new TelemetryClient();
    }

    public void trackEvent(@NonNull String name, Map<String, String> customProperties) {
        final Map<String, String> properties;
        final Map<String, String> defaultProperties = this.getDefaultProperties();

        if (customProperties == null) {
            properties = defaultProperties;
        } else {
            defaultProperties.forEach(customProperties::putIfAbsent);
            properties = customProperties;
        }

        client.trackEvent(name, properties, null);
        client.flush();
    }

    private Map<String, String> getDefaultProperties() {
        final Map<String, String> properties = new HashMap<>();

        properties.put(PROPERTY_VERSION, PROJECT_INFO);
        properties.put(PROPERTY_INSTALLATION_ID, TelemetryUtils.getHashMac());

        return properties;
    }
}
