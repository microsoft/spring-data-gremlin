/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.spring.data.gremlin.common;

import lombok.Getter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyLoader {

    @Getter
    private String propertyFile;
    private Properties properties;
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public PropertyLoader(@NonNull String propertyFile) {
        this.propertyFile = propertyFile;
        this.properties = new Properties();
    }

    private void initializeProperties() {
        final InputStream inputStream = PropertyLoader.class.getResourceAsStream(this.propertyFile);

        try {
            this.properties.load(inputStream);
        } catch (IOException e) {
            log.warning(String.format("Failed to load file %s to property, will omit IOException.", this.propertyFile));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warning(String.format("Unable to close file %s, will omit IOException.", this.propertyFile));
                }
            }
        }
    }

    public String getPropertyValue(@NonNull String propertyName) {
        if (this.properties == null) {
            this.initializeProperties();
        }

        // initializeProperties may failure and leave this.properties still null.
        if (this.properties != null) {
            return this.properties.getProperty(propertyName);
        }

        return "Unknown-Property";
    }
}
