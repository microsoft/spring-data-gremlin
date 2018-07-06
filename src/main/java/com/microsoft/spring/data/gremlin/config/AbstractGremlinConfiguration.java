/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.config;

import com.microsoft.spring.data.gremlin.common.GremlinConfiguration;
import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.query.GremlinTemplate;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryTracker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

public abstract class AbstractGremlinConfiguration extends GremlinConfigurationSupport {

    public abstract GremlinConfiguration getGremlinConfiguration();

    @Bean
    @ConditionalOnProperty(name = "gremlin.telemetryAllowed", havingValue = "true", matchIfMissing = true)
    public TelemetryTracker getTelemetryTracker() {
        return new TelemetryTracker();
    }

    @Bean
    public GremlinFactory gremlinFactory() {
        final GremlinConfiguration config = getGremlinConfiguration();

        return new GremlinFactory(config.getEndpoint(), config.getPort(), config.getUsername(), config.getPassword());
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
