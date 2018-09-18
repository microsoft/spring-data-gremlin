/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.common.domain.Project;
import com.microsoft.spring.data.gremlin.common.domain.Relationship;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinMappingContext;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GremlinScriptLiteralEdgeUnitTest {

    private MappingGremlinConverter converter;
    private GremlinMappingContext mappingContext;
    private GremlinSource gremlinSource;

    @Mock
    private ApplicationContext applicationContext;

    @Before
    public void setup() {
        this.mappingContext = new GremlinMappingContext();
        this.mappingContext.setApplicationContext(this.applicationContext);
        this.mappingContext.afterPropertiesSet();
        this.mappingContext.getPersistentEntity(Person.class);
        this.converter = new MappingGremlinConverter(this.mappingContext);

        final Relationship relationship = new Relationship("456", "rel-name", "china",
                new Person("123", "bill"), // from
                new Project("321", "ms-project", "http") // to
        );
        @SuppressWarnings("unchecked") final GremlinEntityInformation info =
                new GremlinEntityInformation(Relationship.class);
        gremlinSource = info.getGremlinSource();
        this.converter.write(relationship, gremlinSource);
    }

    @Test
    public void testGenerateCountScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateCountScript(gremlinSource);
        assertEquals(queryList.get(0), "g.E()");
    }

    @Test
    public void testGenerateFindByIdScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateFindByIdScript(gremlinSource);
        assertEquals(queryList.get(0), "g.E('456')");
    }

    @Test
    public void testGenerateFindAllScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateFindAllScript(gremlinSource);
        assertEquals(queryList.get(0), "g.E().has(label, 'label-relationship')");
    }

    @Test
    public void testGenerateInsertScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateInsertScript(gremlinSource);
        assertEquals(queryList.get(0), "g.V('123').as('from').V('321').as('to')"
                + ".addE('label-relationship').from('from').to('to')"
                + ".property(id, '456').property('name', 'rel-name').property('location', 'china')");
    }

    @Test
    public void testGenerateUpdateScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateUpdateScript(gremlinSource);
        assertEquals(queryList.get(0), "g.E('456').property('name', 'rel-name').property('location', 'china')");
    }

    @Test
    public void testGenerateDeleteByIdScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateDeleteByIdScript(gremlinSource);
        assertEquals(queryList.get(0), "g.E('456').drop()");
    }

    @Test
    public void testGenerateDeleteAllScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateDeleteAllScript(gremlinSource);
        assertEquals(queryList.get(0), "g.E().drop()");
    }

    @Test
    public void testGenerateDeleteAllByClassScript() {
        final List<String> queryList = new GremlinScriptLiteralEdge().generateDeleteAllByClassScript(gremlinSource);
        assertEquals(queryList.get(0), "g.E().has(label, 'label-relationship').drop()");
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testInvalidDeleteAllByClassScript() {
        new GremlinScriptLiteralEdge().generateDeleteAllByClassScript(new GremlinSourceVertex());
    }

    @Test(expected = GremlinUnexpectedSourceTypeException.class)
    public void testInvalidFindAllScript() {
        new GremlinScriptLiteralEdge().generateFindAllScript(new GremlinSourceVertex());
    }
}
