/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.telemetry;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TelemetryProperties {

    public static final String PROPERTY_VERSION = "version";

    public static final String PROPERTY_INSTALLATION_ID = "installationId";

    public static final String PROPERTY_SERVICE_NAME = "serviceName";
}
