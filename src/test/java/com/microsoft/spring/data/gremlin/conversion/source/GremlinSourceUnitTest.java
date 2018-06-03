/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinMappingContext;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.annotation.Persistent;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class GremlinSourceUnitTest {

    private MappingGremlinConverter converter;

    @Autowired
    private ApplicationContext context;

    @Before
    @SneakyThrows
    public void setup() {
        final GremlinMappingContext mappingContext = new GremlinMappingContext();

        mappingContext.setInitialEntitySet(new EntityScanner(this.context).scan(Persistent.class));

        this.converter = new MappingGremlinConverter(mappingContext);
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testVertexWriteException() {
        new GremlinSourceVertexWriter().write(new Object(), this.converter, new GremlinSourceEdge());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testVertexReadException() {
        new GremlinSourceVertexReader().read(Object.class, this.converter, new GremlinSourceEdge());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testEdgeWriteException() {
        new GremlinSourceEdgeWriter().write(new Object(), this.converter, new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testEdgeReadException() {
        new GremlinSourceEdgeReader().read(Object.class, this.converter, new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testGraphWriteException() {
        new GremlinSourceGraphWriter().write(new Object(), this.converter, new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testGraphReadException() {
        new GremlinSourceEdgeReader().read(Object.class, this.converter, new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testGraphAddSourceException() {
        new GremlinSourceGraph().addGremlinSource(new GremlinSourceGraph());
    }
}
