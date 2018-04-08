/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.exception.UnexpectedGremlinSourceTypeException;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Map;

public class GremlinResultEdgeReader extends BasicGremlinResultReader implements GremlinResultReader {

    public GremlinResultEdgeReader() {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(@NonNull Result result, @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new UnexpectedGremlinSourceTypeException("Should be instance of GremlinSourceEdge");
        }

        Assert.isInstanceOf(Map.class, result.getObject(), "should be one instance of Map");
        final Map<String, Object> map = (Map<String, Object>) result.getObject();

        Assert.isTrue(map.containsKey(Constants.PROPERTY_ID), "should contain id property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_LABEL), "should contain label property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_TYPE), "should contain type property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_PROPERTIES), "should contain properties property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_INV), "should contain inV property");
        Assert.isTrue(map.containsKey(Constants.PROPERTY_OUTV), "should contain outV property");
        Assert.isTrue(map.get(Constants.PROPERTY_TYPE).equals(Constants.RESULT_TYPE_EDGE), "must be vertex type");

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;

        sourceEdge.setId(map.get(Constants.PROPERTY_ID).toString());
        sourceEdge.setLabel(map.get(Constants.PROPERTY_LABEL).toString());
        sourceEdge.setVertexIdFrom(map.get(Constants.PROPERTY_INV).toString());
        sourceEdge.setVertexIdTo(map.get(Constants.PROPERTY_OUTV).toString());

        Assert.isInstanceOf(Map.class, map.get(Constants.PROPERTY_PROPERTIES), "should be one instance of Map");
        final Map<String, Object> properties = (Map<String, Object>) result.getObject();

        super.readResultProperties(properties, source);
    }
}
