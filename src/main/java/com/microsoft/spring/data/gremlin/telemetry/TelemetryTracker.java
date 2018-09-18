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

public class TelemetryTracker {

    private static final String PROJECT_VERSION = TelemetryTracker.class.getPackage().getImplementationVersion();

    private static final String PROJECT_INFO = "spring-data-gremlin" + "/" + PROJECT_VERSION;

    private static final String UNKNOWN_MAC = "unknown-Mac-Address";

    private static final String PROPERTY_VERSION = "version";

    private static final String PROPERTY_INSTALLATION_ID = "installationId";

    private final Map<String, String> properties;

    protected TelemetryClient client;

    public TelemetryTracker() {
        this.client = new TelemetryClient();
        this.properties = new HashMap<>();

        this.properties.put(PROPERTY_VERSION, PROJECT_INFO);
        this.properties.put(PROPERTY_INSTALLATION_ID, getHashMac());
    }

    public void trackEvent(@NonNull String eventName) {
        this.client.trackEvent(eventName, this.properties, null);
        this.client.flush();
    }

    private static String getMacAddress() {
        try {
            final InetAddress host = InetAddress.getLocalHost();
            final byte[] macBytes = NetworkInterface.getByInetAddress(host).getHardwareAddress();

            return Arrays.toString(macBytes);
        } catch (UnknownHostException | SocketException | NullPointerException e) { // Omit
            return UNKNOWN_MAC;
        }
    }

    private static String getHashMac() {
        final String mac = getMacAddress();

        if (mac.equals(UNKNOWN_MAC)) {
            return UNKNOWN_MAC;
        }

        return sha256Hex(mac);
    }
}
