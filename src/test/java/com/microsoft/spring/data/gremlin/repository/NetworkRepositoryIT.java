/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.common.domain.Network;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.common.domain.Project;
import com.microsoft.spring.data.gremlin.common.domain.Relationship;
import com.microsoft.spring.data.gremlin.common.repository.NetworkRepository;
import com.microsoft.spring.data.gremlin.common.repository.PersonRepository;
import com.microsoft.spring.data.gremlin.common.repository.ProjectRepository;
import com.microsoft.spring.data.gremlin.common.repository.RelationshipRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public class NetworkRepositoryIT {

    private final Person person = new Person(TestConstants.VERTEX_PERSON_ID, TestConstants.VERTEX_PERSON_NAME);
    private final Project project = new Project(TestConstants.VERTEX_PROJECT_ID, TestConstants.VERTEX_PROJECT_NAME,
            TestConstants.VERTEX_PROJECT_URI);
    private final Relationship relationship = new Relationship(TestConstants.EDGE_RELATIONSHIP_ID,
            TestConstants.EDGE_RELATIONSHIP_NAME, TestConstants.EDGE_RELATIONSHIP_LOCATION,
            this.person, this.project);

    @Autowired
    private NetworkRepository networkRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Before
    public void setup() {
        this.networkRepository.deleteAll();
    }

    @After
    public void cleanup() {
        this.networkRepository.deleteAll();
    }

    @Test
    public void testDeleteById() {
        final Network network = new Network();

        network.setId("fake-id");
        network.vertexAdd(this.person);
        network.vertexAdd(this.project);
        network.edgeAdd(this.relationship);

        this.networkRepository.save(network);

        Assert.assertTrue(personRepository.findById(this.person.getId()).isPresent());
        Assert.assertTrue(projectRepository.findById(this.project.getId()).isPresent());
        Assert.assertTrue(relationshipRepository.findById(this.relationship.getId()).isPresent());

        this.networkRepository.deleteById(network.getId());

        Assert.assertFalse(personRepository.findById(this.person.getId()).isPresent());
        Assert.assertFalse(projectRepository.findById(this.project.getId()).isPresent());
        Assert.assertFalse(relationshipRepository.findById(this.relationship.getId()).isPresent());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFindAllException() {
        final Network network = new Network();

        network.setId("fake-id");
        network.vertexAdd(this.person);
        network.vertexAdd(this.project);
        network.edgeAdd(this.relationship);

        this.networkRepository.save(network);
        this.networkRepository.findAll(Network.class);
    }
}
