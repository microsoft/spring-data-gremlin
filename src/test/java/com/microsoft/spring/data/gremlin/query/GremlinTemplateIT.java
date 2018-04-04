/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.common.GremlinPropertiesConfiguration;
import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.Network;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.common.domain.Project;
import com.microsoft.spring.data.gremlin.common.domain.Relationship;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.mapping.GremlinMappingContext;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Persistent;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource(value = {"classpath:application.properties"})
@EnableConfigurationProperties(GremlinPropertiesConfiguration.class)
public class GremlinTemplateIT {

    private final Person person = new Person(TestConstants.VERTEX_PERSON_ID, TestConstants.VERTEX_PERSON_NAME);
    private final Person person0 = new Person(TestConstants.VERTEX_PERSON_0_ID, TestConstants.VERTEX_PERSON_0_NAME);
    private final Person person1 = new Person(TestConstants.VERTEX_PERSON_1_ID, TestConstants.VERTEX_PERSON_1_NAME);
    private final Project project = new Project(TestConstants.VERTEX_PROJECT_ID, TestConstants.VERTEX_PROJECT_NAME,
            TestConstants.VERTEX_PROJECT_URI);
    private final Project project0 = new Project(TestConstants.VERTEX_PROJECT_0_ID, TestConstants.VERTEX_PROJECT_0_NAME,
            TestConstants.VERTEX_PROJECT_0_URI);
    private final Relationship relationship = new Relationship(TestConstants.EDGE_RELATIONSHIP_ID,
            TestConstants.EDGE_RELATIONSHIP_NAME, TestConstants.EDGE_RELATIONSHIP_LOCATION,
            this.person, this.project);
    private final Relationship relationship0 = new Relationship(TestConstants.EDGE_RELATIONSHIP_0_ID,
            TestConstants.EDGE_RELATIONSHIP_0_NAME, TestConstants.EDGE_RELATIONSHIP_0_LOCATION,
            this.person0, this.project);
    private final Relationship relationship1 = new Relationship(TestConstants.EDGE_RELATIONSHIP_1_ID,
            TestConstants.EDGE_RELATIONSHIP_1_NAME, TestConstants.EDGE_RELATIONSHIP_1_LOCATION,
            this.person1, this.project);
    private final Relationship relationship2 = new Relationship(TestConstants.EDGE_RELATIONSHIP_2_ID,
            TestConstants.EDGE_RELATIONSHIP_2_NAME, TestConstants.EDGE_RELATIONSHIP_2_LOCATION,
            this.person, this.project0);

    private final Network network = new Network();

    @Autowired
    private GremlinPropertiesConfiguration config;

    @Autowired
    private ApplicationContext context;

    private GremlinTemplate template;

    @Before
    public void setup() throws ClassNotFoundException {
        final GremlinMappingContext mappingContext = new GremlinMappingContext();

        mappingContext.setInitialEntitySet(new EntityScanner(this.context).scan(Persistent.class));

        final MappingGremlinConverter converter = new MappingGremlinConverter(mappingContext);
        final GremlinFactory factory = new GremlinFactory(this.config.getEndpoint(), this.config.getPort(),
                this.config.getUsername(), this.config.getPassword());

        this.template = new GremlinTemplate(factory, converter);

        this.template.deleteAll();

        this.network.vertexAdd(this.person);
        this.network.vertexAdd(this.person0);
        this.network.vertexAdd(this.person1);
        this.network.vertexAdd(this.project);
        this.network.vertexAdd(this.project0);

        this.network.edgeAdd(this.relationship);
        this.network.edgeAdd(this.relationship0);
        this.network.edgeAdd(this.relationship1);
        this.network.edgeAdd(this.relationship2);

        this.template.insert(this.network);
    }

    @Test
    public void testVertexDeleteAll() {
        throw new NotImplementedException("DeleteAll IT of vertex not implemented");
    }
}

