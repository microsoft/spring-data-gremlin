/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.microsoft.spring.data.gremlin.common.Constants.*;
import static com.microsoft.spring.data.gremlin.common.GremlinEntityType.EDGE;
import static com.microsoft.spring.data.gremlin.common.GremlinEntityType.VERTEX;

@NoArgsConstructor
public class GremlinScriptLiteralEdge extends AbstractGremlinScriptLiteral implements GremlinScriptLiteral {

    private static final String FROM_ALIAS = "from";
    private static final String TO_ALIAS = "to";

    private String generateDirection(@NonNull String from, @NonNull String to) {
        return String.format("from('%s').to('%s')", from, to);
    }

    @Override
    public List<String> generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;
        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(generateIdQueryScript(sourceEdge.getVertexIdFrom(), VERTEX));
        scriptList.add(generateAs(FROM_ALIAS));
        scriptList.add(generateIdQueryScript(sourceEdge.getVertexIdTo(), VERTEX));
        scriptList.add(generateAs(TO_ALIAS));
        scriptList.add(generateAddWithLabel(sourceEdge.getLabel(), EDGE));
        scriptList.add(generateDirection(FROM_ALIAS, TO_ALIAS));
        scriptList.add(generateRequiredId(source.getId()));

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteAllScript(@Nullable GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_EDGE_DROP_ALL);
    }

    @Override
    public List<String> generateDeleteAllByClassScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                GREMLIN_PRIMITIVE_EDGE_ALL,
                generateHasLabelScript(source.getLabel()),
                GREMLIN_PRIMITIVE_DROP
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                generateIdQueryScript(source.getId(), EDGE)
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(generateIdQueryScript(source.getId(), EDGE));

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                GREMLIN_PRIMITIVE_EDGE_ALL,
                generateHasLabelScript(source.getLabel())
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,
                generateIdQueryScript(source.getId(), EDGE),
                GREMLIN_PRIMITIVE_DROP
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateCountScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_EDGE_ALL);
    }
}

