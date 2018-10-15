/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository;

import com.google.common.collect.Lists;
import com.microsoft.spring.data.gremlin.annotation.Edge;
import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.annotation.Vertex;
import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.common.domain.Master;
import com.microsoft.spring.data.gremlin.common.domain.Student;
import com.microsoft.spring.data.gremlin.common.repository.MasterRepository;
import com.microsoft.spring.data.gremlin.common.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public class MasterRepositoryIT {

    private static final Long ID_STUDENT = 1L;
    private static final Long ID_MASTER = 2L;

    private static final String NAME = "name";

    private static final Student STUDENT = new Student(ID_STUDENT, NAME);

    private static final Master MASTER = new Master(ID_MASTER, NAME);

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Before
    public void setup() {
        this.masterRepository.deleteAll();
        this.studentRepository.deleteAll();
    }

    @After
    public void cleanup() {
        this.masterRepository.deleteAll();
        this.studentRepository.deleteAll();
    }

    @Test
    public void testDuplicatedLabelFindAll() {
        this.studentRepository.save(STUDENT);
        this.masterRepository.save(MASTER);

        final List<Master> masters = Lists.newArrayList(this.masterRepository.findAll(Master.class));

        Assert.assertEquals(masters.size(), 1);
        Assert.assertEquals(masters.get(0), MASTER);

        final List<Student> students = Lists.newArrayList(this.studentRepository.findAll(Student.class));

        Assert.assertEquals(students.size(), 1);
        Assert.assertEquals(students.get(0), STUDENT);
    }

//    @Autowired
//    private TestVertexRepository testVertexRepository;
//
//    @Test
//    public void testPredefinedProperty() {
//        testVertexRepository.save(new TestVertex("id", "name"));
//    }
//
//    @Vertex
//    @Data
//    private static class TestVertex {
//
//        private String id;
//
//        private String _classname;
//    }
//
//    @Edge
//    @Data
//    private static class TestEdge {
//
//        private String id;
//
//        private String _classname;
//
//        @EdgeFrom
//        private TestVertex from;
//
//        @EdgeTo
//        private TestVertex to;
//    }
//
//    private interface TestVertexRepository extends GremlinRepository<TestVertex, String> {
//
//    }
}
