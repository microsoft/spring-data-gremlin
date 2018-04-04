/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import org.springframework.lang.NonNull;

public abstract class GremlinUtils {
    public static <T> T createInstance(@NonNull Class<T> type) {
       final T instance;

       try {
           instance = type.newInstance();
       } catch (IllegalAccessException e) {
           throw new IllegalArgumentException("can not access type constructor", e);
       } catch (InstantiationException e) {
           throw new IllegalArgumentException("failed to create instance of given type", e);
       }

       return instance;
    }
}
