/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultEdgeReader;
import com.microsoft.spring.data.gremlin.conversion.result.GremlinResultVertexReader;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GremlinScriptUnitTest {

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testVertexWriteException() {
        new GremlinResultVertexReader().read(new Result(new Object()), new GremlinSourceEdge());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testEdgeReadException() {
        new GremlinResultEdgeReader().read(new Result(new Object()), new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedEntityTypeException.class)
    public void testGeneratePropertyException() {
        final Map<String, Object> properties = new HashMap<>();
        final GremlinSource source = new GremlinSourceVertex();

        properties.put("person", source);

        new GremlinScriptLiteralVertex().generateProperties(properties);
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testEdgeDeleteByIdScriptException() {
        new GremlinScriptLiteralEdge().generateDeleteByIdScript(new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testGraphDeleteByIdScriptException() {
        new GremlinScriptLiteralGraph().generateDeleteByIdScript(new GremlinSourceVertex());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGraphFindAllScriptException() {
        new GremlinScriptLiteralGraph().generateFindAllScript(new GremlinSourceVertex());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGraphCountException() {
        new GremlinScriptLiteralGraph().generateCountScript(new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testEdgeCountScriptException() {
        new GremlinScriptLiteralEdge().generateCountScript(new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testVertexDeleteByIdScriptException() {
        new GremlinScriptLiteralGraph().generateDeleteByIdScript(new GremlinSourceEdge());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testVertexCountScriptException() {
        new GremlinScriptLiteralVertex().generateCountScript(new GremlinSourceEdge());
    }
}
