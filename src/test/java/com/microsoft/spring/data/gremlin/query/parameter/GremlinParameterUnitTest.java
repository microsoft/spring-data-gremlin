/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.parameter;

import com.microsoft.spring.data.gremlin.query.paramerter.GremlinParameter;
import com.microsoft.spring.data.gremlin.query.paramerter.GremlinParameters;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

public class GremlinParameterUnitTest {

    private Method method;
    private MethodParameter methodParameter;

    public String handle(@NonNull String name) {
        return "handle: " + name;
    }

    @Before
    @SneakyThrows
    public void setup() {
        method = this.getClass().getMethod("handle", String.class);
        methodParameter = new MethodParameter(this.getClass().getMethod("handle", String.class), 0);
    }

    @Test
    public void testGremlinParameter() {
        final GremlinParameter parameter = new GremlinParameter(this.methodParameter);

        Assert.assertNotNull(parameter);
        Assert.assertEquals(parameter.getType(), String.class);
        Assert.assertEquals(parameter.getIndex(), 0);
    }

    @Test
    public void testGremlinParameters() {
        final GremlinParameters gremlinParameters = new GremlinParameters(this.method);

        Assert.assertNotNull(gremlinParameters);
        Assert.assertEquals(gremlinParameters.getNumberOfParameters(), 1);
        Assert.assertNotNull(gremlinParameters.getParameter(0));
    }
}

