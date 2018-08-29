/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.telemetry;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.lang.NonNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.microsoft.applicationinsights.core.dependencies.apachecommons.codec.digest.DigestUtils.sha256Hex;
import static com.microsoft.spring.data.gremlin.telemetry.TelemetryProperties.PROPERTY_INSTALLATION_ID;
import static com.microsoft.spring.data.gremlin.telemetry.TelemetryProperties.PROPERTY_SERVICE_NAME;
import static com.microsoft.spring.data.gremlin.telemetry.TelemetryProperties.PROPERTY_VERSION;

public class TelemetryTracker {

    private static final String PROJECT_VERSION = TelemetryTracker.class.getPackage().getImplementationVersion();

    private static final String PROJECT_INFO = "spring-data-gremlin" + "/" + PROJECT_VERSION;

    private static final String SERVICE_NAME = "gremlin";

    private static final String UNKNOWN_MAC = "unknown-Mac-Address";

    protected TelemetryClient client;

    public TelemetryTracker() {
        this.client = new TelemetryClient();
    }

    public void trackEvent(@NonNull String name) {
        final Map<String, String> properties = this.getDefaultProperties();

        properties.put(PROPERTY_INSTALLATION_ID, getHashMac());

        client.trackEvent(name, properties, null);
        client.flush();
    }

    private Map<String, String> getDefaultProperties() {
        final Map<String, String> properties = new HashMap<>();

        properties.put(PROPERTY_VERSION, PROJECT_INFO);
        properties.put(PROPERTY_SERVICE_NAME, SERVICE_NAME);

        return properties;
    }

    private static String getMacAddress() {
        final InetAddress ip;
        final NetworkInterface network;
        final byte[] macBytes;

        try {
            ip = InetAddress.getLocalHost();
            network = NetworkInterface.getByInetAddress(ip);
            macBytes = network.getHardwareAddress();
        } catch (UnknownHostException | SocketException e) { // Omit
            return UNKNOWN_MAC;
        }

        return Arrays.toString(macBytes);
    }

    private static String getHashMac() {
        final String mac = getMacAddress();

        if (mac.equals(UNKNOWN_MAC)) {
            return UNKNOWN_MAC;
        }

        return sha256Hex(mac);
    }
}
