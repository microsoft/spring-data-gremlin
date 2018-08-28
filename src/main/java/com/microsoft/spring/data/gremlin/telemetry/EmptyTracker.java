/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.telemetry;

import org.springframework.lang.NonNull;

import java.util.Map;

public class EmptyTracker extends TelemetryTracker {
    @Override
    public void trackEvent(@NonNull String name, Map<String, String> customProperties) {
        // Do nothing
    }
}
