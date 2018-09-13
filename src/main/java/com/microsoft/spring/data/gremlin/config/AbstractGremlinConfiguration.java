/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.config;

import com.microsoft.spring.data.gremlin.common.GremlinConfig;
import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.query.GremlinTemplate;
import com.microsoft.spring.data.gremlin.telemetry.EmptyTracker;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryTracker;
import org.springframework.context.annotation.Bean;

public abstract class AbstractGremlinConfiguration extends GremlinConfigurationSupport {

    public abstract GremlinConfig getGremlinConfig();

    @Bean
    public TelemetryTracker getTelemetryTracker() {
        if (getGremlinConfig().isTelemetryAllowed()) {
            return new TelemetryTracker();
        }

        return new EmptyTracker();
    }

    @Bean
    public GremlinFactory gremlinFactory() {
        return new GremlinFactory(getGremlinConfig());
    }

    @Bean
    public MappingGremlinConverter mappingGremlinConverter() throws ClassNotFoundException {
        return new MappingGremlinConverter(gremlinMappingContext());
    }

    @Bean
    public GremlinTemplate gremlinTemplate(GremlinFactory factory) throws ClassNotFoundException {
        return new GremlinTemplate(factory, mappingGremlinConverter());
    }
}
