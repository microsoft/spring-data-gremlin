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
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.microsoft.spring.data.gremlin.common.Constants.*;
import static com.microsoft.spring.data.gremlin.common.GremlinEntityType.VERTEX;


@NoArgsConstructor
public class GremlinScriptLiteralVertex extends AbstractGremlinScriptLiteral implements GremlinScriptLiteral {

    @Override
    public List<String> generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(generateAddWithLabel(source.getLabel(), VERTEX));
        scriptList.add(generateRequiredId(source.getId()));

        scriptList.addAll(super.generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_VERTEX_DROP_ALL);
    }

    @Override
    public List<String> generateDeleteAllByClassScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                GREMLIN_PRIMITIVE_VERTEX_ALL,
                generateHasLabelScript(source.getLabel()),
                GREMLIN_PRIMITIVE_DROP
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                generateIdQueryScript(source.getId(), VERTEX)
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(generateIdQueryScript(source.getId(), VERTEX));

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                GREMLIN_PRIMITIVE_VERTEX_ALL,
                generateHasLabelScript(source.getLabel())
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                generateIdQueryScript(source.getId(), VERTEX),
                GREMLIN_PRIMITIVE_DROP
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateCountScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_VERTEX_ALL);
    }
}

