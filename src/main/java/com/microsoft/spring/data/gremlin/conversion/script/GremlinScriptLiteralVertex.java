/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;

@NoArgsConstructor
public class GremlinScriptLiteralVertex extends AbstractGremlinScriptLiteral implements GremlinScriptLiteral {

    @Override
    public List<String> generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();
        final String label = source.getLabel();
        final String id = source.getId();
        final Map<String, Object> properties = source.getProperties();

        Assert.notNull(label, "label should not be null");
        Assert.notNull(id, "id should not be null");
        Assert.notNull(properties, "properties should not be null");

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_ADD_VERTEX, label));
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_STRING, Constants.PROPERTY_ID, id));

        scriptList.addAll(super.generateProperties(properties));

        final String query = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);

        return Collections.singletonList(query);
    }

    @Override
    public List<String> generateDeleteAllScript(@Nullable GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_VERTEX_DROP_ALL);
    }

    @Override
    public List<String> generateFindByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();
        final String id = source.getId();

        Assert.notNull(id, "id should not be null");

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_VERTEX, id));

        final String query = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);

        return Collections.singletonList(query);
    }

    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();
        final String id = source.getId();
        final Map<String, Object> properties = source.getProperties();

        Assert.notNull(id, "id should not be null");
        Assert.notNull(properties, "properties should not be null");

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_VERTEX, id));

        scriptList.addAll(super.generateProperties(properties));

        final String query = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);

        return Collections.singletonList(query);
    }

    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        final String label = source.getLabel();
        final List<String> scriptList = new ArrayList<>();

        Assert.notNull(label, "label should not be null");

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(Constants.GREMLIN_PRIMITIVE_VERTEX_ALL);
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_HAS, Constants.PROPERTY_LABEL, label));

        final String query = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);

        return Collections.singletonList(query);
    }

    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();
        final String id = source.getId();

        Assert.notNull(id, "id should not be null");

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_VERTEX, id));
        scriptList.add(Constants.GREMLIN_PRIMITIVE_DROP);

        final String query = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);

        return Collections.singletonList(query);
    }

    @Override
    public List<String> generateCountScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_VERTEX_ALL);
    }
}

