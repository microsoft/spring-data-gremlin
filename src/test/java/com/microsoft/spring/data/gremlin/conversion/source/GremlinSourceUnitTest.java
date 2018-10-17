/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.annotation.Vertex;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinEntityInformationException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinMappingContext;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    @Test(expected = GremlinEntityInformationException.class)
    public void testVertexWithPredefinedProperty() {
        @SuppressWarnings("unchecked") final GremlinSource source = new GremlinSourceVertex(TestVertex.class);

        new GremlinSourceVertexWriter().write(new TestVertex("fake-id", "fake-name"), this.converter, source);
    }

    @Test(expected = GremlinEntityInformationException.class)
    public void testEdgeWithPredefinedProperty() {
        @SuppressWarnings("unchecked") final GremlinSource source = new GremlinSourceEdge(TestEdge.class);

        new GremlinSourceEdgeWriter().write(new TestEdge("fake-id", "fake-name", "1", "2"), this.converter, source);
    }

    @Vertex
    @Data
    @AllArgsConstructor
    private static class TestVertex {

        private String id;

        private String _classname;
    }

    @Vertex
    @Data
    @AllArgsConstructor
    private static class TestEdge {

        private String id;

        private String _classname;

        @EdgeFrom
        private String from;

        @EdgeTo
        private String to;
    }
}
