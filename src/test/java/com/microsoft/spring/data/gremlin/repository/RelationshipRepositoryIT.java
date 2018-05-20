/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.common.domain.Project;
import com.microsoft.spring.data.gremlin.common.domain.Relationship;
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

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public class RelationshipRepositoryIT {

    private final Person person = new Person(TestConstants.VERTEX_PERSON_ID, TestConstants.VERTEX_PERSON_NAME);
    private final Person person0 = new Person(TestConstants.VERTEX_PERSON_0_ID, TestConstants.VERTEX_PERSON_0_NAME);
    private final Project project = new Project(TestConstants.VERTEX_PROJECT_ID, TestConstants.VERTEX_PROJECT_NAME,
            TestConstants.VERTEX_PROJECT_URI);
    private final Relationship relationship = new Relationship(TestConstants.EDGE_RELATIONSHIP_ID,
            TestConstants.EDGE_RELATIONSHIP_NAME, TestConstants.EDGE_RELATIONSHIP_LOCATION, this.person, this.project);

    @Autowired
    private RelationshipRepository relationshipRepo;

    @Autowired
    private PersonRepository personRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Before
    public void setup() {
        this.relationshipRepo.deleteAll();
    }

    @After
    public void cleanup() {
        this.relationshipRepo.deleteAll();
    }

    @Test
    public void testDeleteAll() {
        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        Assert.assertTrue(this.relationshipRepo.existsById(this.relationship.getId()));

        this.relationshipRepo.deleteAll();

        Assert.assertFalse(this.relationshipRepo.existsById(this.person.getId()));
    }

    @Test
    public void testDeleteById() {
        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        Assert.assertTrue(this.relationshipRepo.existsById(this.relationship.getId()));

        this.relationshipRepo.deleteById(this.relationship.getId());

        Assert.assertFalse(this.relationshipRepo.existsById(this.relationship.getId()));
    }

    @Test
    public void testDelete() {
        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        Assert.assertTrue(this.relationshipRepo.existsById(this.relationship.getId()));

        this.relationshipRepo.delete(this.relationship);

        Assert.assertFalse(this.relationshipRepo.existsById(this.relationship.getId()));
    }

    @Test
    public void testDeleteAllIds() {
        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        final List<Relationship> domains = Arrays.asList(this.relationship);

        this.relationshipRepo.deleteAll(domains);

        Assert.assertFalse(this.relationshipRepo.existsById(this.relationship.getId()));
    }

    @Test
    public void testSave() {
        Assert.assertFalse(this.relationshipRepo.existsById(this.relationship.getId()));

        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        Assert.assertTrue(this.relationshipRepo.existsById(this.relationship.getId()));
    }

    @Test
    public void testSaveAll() {
        Assert.assertFalse(this.relationshipRepo.existsById(this.relationship.getId()));

        final List<Relationship> domains = Arrays.asList(this.relationship);

        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.saveAll(domains);

        Assert.assertTrue(this.relationshipRepo.existsById(this.relationship.getId()));
    }

    @Test
    public void testFindById() {
        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        final Relationship foundRelationship = this.relationshipRepo.findById(this.relationship.getId()).get();

        Assert.assertNotNull(foundRelationship);
        Assert.assertEquals(foundRelationship.getId(), this.relationship.getId());
        Assert.assertEquals(foundRelationship.getName(), this.relationship.getName());

        Assert.assertFalse(this.relationshipRepo.findById(this.person.getId()).isPresent());
    }

    @Test
    public void testExistById() {
        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        Assert.assertFalse(this.relationshipRepo.existsById(this.person.getId()));
        Assert.assertTrue(this.relationshipRepo.existsById(this.relationship.getId()));
    }


    @Test
    public void testFindAllById() {
        final List<Relationship> domains = Arrays.asList(this.relationship);
        final List<String> ids = Arrays.asList(this.relationship.getId());

        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.saveAll(domains);

        final List<Relationship> foundDomains = (List<Relationship>) this.relationshipRepo.findAllById(ids);

        domains.sort((a, b) -> (a.getId().compareTo(b.getId())));
        foundDomains.sort((a, b) -> (a.getId().compareTo(b.getId())));

        Assert.assertArrayEquals(domains.toArray(), foundDomains.toArray());
    }

    @Test
    public void testVertexCount() {
        Assert.assertEquals(this.personRepo.count(), 0);
        Assert.assertEquals(this.projectRepo.edgeCount(), 0);
        Assert.assertEquals(this.relationshipRepo.vertexCount(), 0);

        this.personRepo.save(this.person0);
        this.personRepo.save(this.person);
        this.projectRepo.save(this.project);
        this.relationshipRepo.save(this.relationship);

        Assert.assertEquals(this.personRepo.vertexCount(), 3);
        Assert.assertEquals(this.projectRepo.vertexCount(), 3);
        Assert.assertEquals(this.relationshipRepo.edgeCount(), 1);
        Assert.assertEquals(this.relationshipRepo.count(), 4);
    }
}

