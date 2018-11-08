/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.util.Assert;

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

    private String generateEdgeDirection(@NonNull String from, @NonNull String to) {
        return String.format("from('%s').to('%s')", from, to);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;
        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                                            // g
        scriptList.add(generateEntityWithRequiredId(sourceEdge.getVertexIdFrom(), VERTEX)); // V(id)
        scriptList.add(generateAsWithAlias(FROM_ALIAS));                                    // from('from')
        scriptList.add(generateEntityWithRequiredId(sourceEdge.getVertexIdTo(), VERTEX));   // V(id)
        scriptList.add(generateAsWithAlias(TO_ALIAS));                                      // to('to')
        scriptList.add(generateAddEntityWithLabel(sourceEdge.getLabel(), EDGE));            // addE(label)
        scriptList.add(generateEdgeDirection(FROM_ALIAS, TO_ALIAS));                        // from('from').to('to')

        source.getId().ifPresent(id -> scriptList.add(generatePropertyWithRequiredId(id))); // property(id, xxx)

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteAllScript() {
        return Collections.singletonList(GREMLIN_SCRIPT_EDGE_DROP_ALL);
    }

    @Override
    public List<String> generateDeleteAllByClassScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,             // g
                GREMLIN_PRIMITIVE_EDGE_ALL,          // E()
                generateHasLabel(source.getLabel()), // has(label, 'label')
                GREMLIN_PRIMITIVE_DROP               // drop()
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        Assert.isTrue(source.getId().isPresent(), "GremlinSource should contain id.");

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                                 // g
                GREMLIN_PRIMITIVE_EDGE_ALL,                              // E()
                generateHasId(source.getId().get(), source.getIdField()) // hasId(xxx)
        );

        return completeScript(scriptList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = new ArrayList<>();

        Assert.isTrue(source.getId().isPresent(), "GremlinSource should contain id.");

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                                  // g
        scriptList.add(generateEntityWithRequiredId(source.getId().get(), EDGE)); // E(id)

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final String className = source.getProperties().get(GREMLIN_PROPERTY_CLASSNAME).toString();
        Assert.notNull(className, "GremlinSource should contain predefined className");

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                           // g
                GREMLIN_PRIMITIVE_EDGE_ALL,                        // E()
                generateHasLabel(source.getLabel()),               // has(label, 'label')
                generateHas(GREMLIN_PROPERTY_CLASSNAME, className) // has(_classname, 'xxxxxx')
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        Assert.isTrue(source.getId().isPresent(), "GremlinSource should contain id.");

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                                  // g
                GREMLIN_PRIMITIVE_EDGE_ALL,                               // E()
                generateHasId(source.getId().get(), source.getIdField()), // hasId(xxx)
                GREMLIN_PRIMITIVE_DROP                                    // drop()
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateCountScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        return Collections.singletonList(GREMLIN_SCRIPT_EDGE_ALL);
    }
}

