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
import com.microsoft.spring.data.gremlin.exception.GremlinQueryException;
import com.microsoft.spring.data.gremlin.mapping.GremlinMappingContext;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Assert;
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
    @SneakyThrows
    public void setup() {
        final GremlinMappingContext mappingContext = new GremlinMappingContext();
        final GremlinFactory factory = new GremlinFactory(this.config.getEndpoint(), this.config.getPort(),
                this.config.getUsername(), this.config.getPassword());

        mappingContext.setInitialEntitySet(new EntityScanner(this.context).scan(Persistent.class));

        final MappingGremlinConverter converter = new MappingGremlinConverter(mappingContext);

        this.template = new GremlinTemplate(factory, converter);
        this.template.deleteAll();
    }

    private void buildTestGraph() {
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

    @After
    public void cleanup() {
        this.template.deleteAll();
    }

    @Test
    @SneakyThrows
    public void testVertexDeleteAll() {
        this.buildTestGraph();

        Person personVertex = this.template.findVertexById(this.person.getId(), Person.class);
        Project projectVertex = this.template.findVertexById(this.project.getId(), Project.class);
        Relationship relationshipEdge = this.template.findEdgeById(this.relationship.getId(), Relationship.class);

        Assert.assertNotNull(personVertex);
        Assert.assertNotNull(projectVertex);
        Assert.assertNotNull(relationshipEdge);

        this.template.deleteAll();

        personVertex = this.template.findVertexById(this.person.getId(), Person.class);
        projectVertex = this.template.findVertexById(this.project.getId(), Project.class);
        relationshipEdge = this.template.findEdgeById(this.relationship.getId(), Relationship.class);

        Assert.assertNull(personVertex);
        Assert.assertNull(projectVertex);
        Assert.assertNull(relationshipEdge);

        // Todo(pan): should add findVertexAll here.
    }

    @Test
    public void testVertexInsertNormal() {
        this.template.insert(this.person0);

        final Person foundPerson = this.template.findVertexById(this.person0.getId(), Person.class);

        Assert.assertNotNull(foundPerson);
        Assert.assertEquals(foundPerson.getId(), this.person0.getId());
        Assert.assertEquals(foundPerson.getName(), this.person0.getName());
    }

    @Test(expected = GremlinQueryException.class)
    public void testVertexInsertException() {
        this.template.insert(this.person);

        final Person repeated = new Person(this.person.getId(), this.person.getName());
        this.template.insert(repeated);
    }

    @Test
    public void testFindVertexById() {
        Person foundPerson = this.template.findVertexById(this.person1.getId(), Person.class);
        Assert.assertNull(foundPerson);

        this.template.insert(this.person1);
        foundPerson = this.template.findVertexById(this.person1.getId(), Person.class);

        Assert.assertNotNull(foundPerson);
        Assert.assertEquals(foundPerson.getId(), this.person1.getId());
        Assert.assertEquals(foundPerson.getName(), this.person1.getName());
    }

    @Test
    public void testFindEdgeById() {
        Relationship foundRelationship = this.template.findEdgeById(this.relationship2.getId(), Relationship.class);
        Assert.assertNull(foundRelationship);

        this.template.insert(this.person);
        this.template.insert(this.project0);
        this.template.insert(this.relationship2);
        foundRelationship = this.template.findEdgeById(this.relationship2.getId(), Relationship.class);

        Assert.assertNotNull(foundRelationship);
        Assert.assertEquals(foundRelationship.getId(), this.relationship2.getId());
        Assert.assertEquals(foundRelationship.getName(), this.relationship2.getName());
        Assert.assertEquals(foundRelationship.getLocation(), this.relationship2.getLocation());
    }

    @Test
    public void testFindById() {
        this.buildTestGraph();
        final Person foundPerson = this.template.findById(this.person1.getId(), Person.class);

        Assert.assertNotNull(foundPerson);
        Assert.assertEquals(foundPerson.getId(), this.person1.getId());
        Assert.assertEquals(foundPerson.getName(), this.person1.getName());

        final Relationship foundRelationship = this.template.findById(this.relationship.getId(), Relationship.class);

        Assert.assertNotNull(foundRelationship);
        Assert.assertEquals(foundRelationship.getId(), this.relationship.getId());
        Assert.assertEquals(foundRelationship.getName(), this.relationship.getName());
        Assert.assertEquals(foundRelationship.getLocation(), this.relationship.getLocation());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFindByIdException() {
        final Network foundNetwork = this.template.findById(this.network.getId(), Network.class);
    }
}

