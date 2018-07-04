/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.telemetry;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

import static com.microsoft.applicationinsights.core.dependencies.apachecommons.codec.digest.DigestUtils.sha256Hex;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TelemetryUtils {

    private static final String UNKNOWN_MAC = "unknown-Mac-Address";

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

    public static String getHashMac() {
        final String mac = getMacAddress();

        if (mac.equals(UNKNOWN_MAC)) {
            return UNKNOWN_MAC;
        }

        return sha256Hex(mac);
    }

    public static void telemetryTriggerEvent(TelemetryTracker tracker, @NonNull String eventName,
                                             Map<String, String> properties) {
        if (tracker != null) {
            tracker.trackEvent(eventName, properties);
        }
    }
}
