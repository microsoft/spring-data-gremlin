/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.result;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Map;

public class GremlinResultVertexReader extends AbstractGremlinResultReader implements GremlinResultReader {

    public GremlinResultVertexReader() {
        super();
    }

    @Override
    public void read(@NonNull Result result, @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("Should be instance of GremlinSourceVertex");
        }

        Assert.isInstanceOf(Map.class, result.getObject(), "should be one instance of Map");
        @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) result.getObject();

        Assert.isTrue(map.containsKey(Constants.PROPERTY_ID), "should contain id property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_LABEL), "should contain label property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_TYPE), "should contain type property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_PROPERTIES), "should contain properties property");
        Assert.isTrue(map.get(Constants.PROPERTY_TYPE).equals(Constants.RESULT_TYPE_VERTEX), "must be vertex type");

        source.setId(map.get(Constants.PROPERTY_ID));
        source.setLabel(map.get(Constants.PROPERTY_LABEL).toString());

        Assert.isInstanceOf(Map.class, map.get(Constants.PROPERTY_PROPERTIES), "should be one instance of Map");
        @SuppressWarnings("unchecked") final Map<String, Object> properties =
                (Map<String, Object>) map.get(Constants.PROPERTY_PROPERTIES);

        source.getProperties().clear();
        super.readResultProperties(properties, source);
    }
}
