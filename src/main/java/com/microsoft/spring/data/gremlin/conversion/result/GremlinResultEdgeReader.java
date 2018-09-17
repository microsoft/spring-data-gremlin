/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.result;

import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Map;

import static com.microsoft.spring.data.gremlin.common.Constants.*;

public class GremlinResultEdgeReader extends AbstractGremlinResultReader implements GremlinResultReader {

    public GremlinResultEdgeReader() {
        super();
    }

    private void readProperties(@NonNull GremlinSource source, @Nullable Map map) {
        if (map != null) {
            @SuppressWarnings("unchecked") final Map<String, Object> properties = (Map<String, Object>) map;

            properties.forEach(source::setProperty);
        }
    }


    @Override
    public void read(@NonNull Result result, @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("Should be instance of GremlinSourceEdge");
        }

        Assert.isInstanceOf(Map.class, result.getObject(), "should be one instance of Map");
        @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) result.getObject();

        Assert.isTrue(map.containsKey(PROPERTY_ID), "should contain id property");
        Assert.isTrue(map.containsKey(PROPERTY_LABEL), "should contain label property");
        Assert.isTrue(map.containsKey(PROPERTY_TYPE), "should contain type property");
        Assert.isTrue(map.containsKey(PROPERTY_INV), "should contain inV property");
        Assert.isTrue(map.containsKey(PROPERTY_OUTV), "should contain outV property");
        Assert.isTrue(map.get(PROPERTY_TYPE).equals(RESULT_TYPE_EDGE), "must be vertex type");

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;

        sourceEdge.setId(map.get(PROPERTY_ID));
        sourceEdge.setLabel(map.get(PROPERTY_LABEL).toString());
        sourceEdge.setVertexIdFrom(map.get(PROPERTY_OUTV));
        sourceEdge.setVertexIdTo(map.get(PROPERTY_INV));

        this.readProperties(source, (Map) map.get(PROPERTY_PROPERTIES));
    }
}
