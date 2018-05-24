/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyLoader {

    private static final String PROJECT_PROPERTY_FILE = "/META-INF/project.properties";
    private static final String APPLICATION_PROPERTY_FILE = "/application.properties";
    private static final String APPLICATION_YML_FILE = "/application.yml";
    private static final Logger LOGGER = Logger.getLogger(PropertyLoader.class.getSimpleName());

    public static String getProjectVersion() {
        return getPropertyByName("project.version", PROJECT_PROPERTY_FILE);
    }

    public static String getApplicationTelemetryAllowed() {
        String allowed = getPropertyByName("documentdb.telemetryAllowed", APPLICATION_PROPERTY_FILE);

        if (allowed == null) {
            allowed = getPropertyByName("telemetryAllowed", APPLICATION_YML_FILE);
        }

        return allowed;
    }

    private static String getPropertyByName(@NonNull String name, @NonNull String filename) {
        String property = "unknown";
        final InputStream inputStream = PropertyLoader.class.getResourceAsStream(filename);
        final Properties properties = new Properties();

        try {
            properties.load(inputStream);
            property = properties.getProperty(name);
        } catch (IOException e) {
            LOGGER.warning(String.format("Failed to load file %s to property, will omit IOException.", filename));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.warning(String.format("Unable to close file %s, will omit IOException.", filename));
                }
            }
        }

        return property;
    }
}
