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

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                               // g
        scriptList.add(generateAddEntityWithLabel(source.getLabel(), VERTEX)); // addV('label')
        if (source.getId() != null) {
             scriptList.add(generatePropertyWithRequiredId(source.getId()));        // property(id, xxx)
        }

        scriptList.addAll(generateProperties(source.getProperties()));

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
                GREMLIN_PRIMITIVE_GRAPH,             // g
                GREMLIN_PRIMITIVE_VERTEX_ALL,        // V()
                generateHasLabel(source.getLabel()), // has(label, 'label')
                GREMLIN_PRIMITIVE_DROP               // drop()
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                             // g
                generateEntityWithRequiredId(source.getId(), VERTEX) // V(id)
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                              // g
        scriptList.add(generateEntityWithRequiredId(source.getId(), VERTEX)); // V(id)

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,            // g
                GREMLIN_PRIMITIVE_VERTEX_ALL,       // V()
                generateHasLabel(source.getLabel()) // has(label, 'label')
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                              // g
                generateEntityWithRequiredId(source.getId(), VERTEX), // V(id)
                GREMLIN_PRIMITIVE_DROP                                // drop()
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

