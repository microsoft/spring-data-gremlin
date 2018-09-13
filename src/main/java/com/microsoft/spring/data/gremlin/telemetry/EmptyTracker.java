/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.telemetry;

import org.springframework.lang.NonNull;

public class EmptyTracker extends TelemetryTracker {
    public EmptyTracker() {
        this.client = null;
    }

    @Override
    public void trackEvent(@NonNull String name) {
        // Do nothing
    }
}
