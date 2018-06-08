/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository;

import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.common.domain.Service;
import com.microsoft.spring.data.gremlin.common.domain.ServiceType;
import com.microsoft.spring.data.gremlin.common.domain.SimpleDependency;
import com.microsoft.spring.data.gremlin.common.repository.ServiceRepository;
import com.microsoft.spring.data.gremlin.common.repository.SimpleDependencyRepository;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.text.SimpleDateFormat;

import static com.microsoft.spring.data.gremlin.common.domain.ServiceType.BACK_END;
import static com.microsoft.spring.data.gremlin.common.domain.ServiceType.FRONT_END;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public class ServiceRepositoryIT {

    private static final Map<String, Object> PROPERTIES_A = new HashMap<>();
    private static final Map<String, Object> PROPERTIES_B = new HashMap<>();

    private static final String ID_A = "1234";
    private static final String ID_B = "8731";

    private static final int COUNT_A = 2;
    private static final int COUNT_B = 8;

    private static final String NAME_A = "name-A";
    private static final String NAME_B = "name-B";

    private static Service serviceA;
    private static Service serviceB;

    private static Date createDateA;
    private static Date createDateB;

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private SimpleDependencyRepository dependencyRepo;

    @BeforeClass
    @SneakyThrows
    public static void initialize() {
        PROPERTIES_B.put("serviceB-port", 8761);
        PROPERTIES_B.put("priority", "high");
        PROPERTIES_B.put("enabled-hystrix", false);

        PROPERTIES_A.put("serviceA-port", 8888);
        PROPERTIES_A.put("serviceB-port", 8761);
        PROPERTIES_A.put("priority", "highest");

        createDateA = new SimpleDateFormat("yyyyMMdd").parse("20180601");
        createDateB = new SimpleDateFormat("yyyyMMdd").parse("20180603");

        serviceA = new Service(ID_A, COUNT_A, true, NAME_A, FRONT_END, createDateA, PROPERTIES_A);
        serviceB = new Service(ID_B, COUNT_B, false, NAME_B, BACK_END, createDateB, PROPERTIES_B);
    }

    @Before
    public void setup() {
        this.repository.deleteAll();
    }

    @After
    public void cleanup() {
        this.repository.deleteAll();
    }

    @Test
    public void testQueries() {
        Assert.assertFalse(this.repository.findById(serviceA.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(serviceB.getId()).isPresent());

        this.repository.save(serviceA);
        this.repository.save(serviceB);

        Optional<Service> foundOptional = this.repository.findById(serviceA.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), serviceA);

        foundOptional = this.repository.findById(serviceB.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), serviceB);

        this.repository.deleteById(serviceA.getId());
        this.repository.deleteById(serviceB.getId());

        Assert.assertFalse(this.repository.findById(serviceA.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(serviceB.getId()).isPresent());
    }

    @Test
    public void testEdgeFromToStringId() {
        final SimpleDependency depend = new SimpleDependency("fakeId", "faked", serviceA.getId(), serviceB.getId());

        this.repository.save(serviceA);
        this.repository.save(serviceB);
        this.dependencyRepo.save(depend);

        final Optional<SimpleDependency> foundOptional = this.dependencyRepo.findById(depend.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), depend);

        this.dependencyRepo.delete(foundOptional.get());
        Assert.assertTrue(this.repository.findById(serviceA.getId()).isPresent());
        Assert.assertTrue(this.repository.findById(serviceB.getId()).isPresent());
    }

    @Test
    public void testFindByName() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByName(serviceA.getName());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), serviceA);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByName(serviceA.getName()).isEmpty());
    }

    @Test
    public void testFindByInstanceCount() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByInstanceCount(serviceB.getInstanceCount());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), serviceB);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByInstanceCount(serviceB.getInstanceCount()).isEmpty());
    }

    @Test
    public void testFindByIsActive() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByIsActive(serviceB.isActive());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), serviceB);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByIsActive(serviceB.isActive()).isEmpty());
    }

    @Test
    public void testFindByCreateAt() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByCreateAt(serviceA.getCreateAt());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), serviceA);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByCreateAt(serviceB.getCreateAt()).isEmpty());
    }

    @Test
    public void testFindByProperties() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByProperties(serviceB.getProperties());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), serviceB);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByProperties(serviceB.getProperties()).isEmpty());
    }

    @Test
    public void testFindById() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final Optional<Service> foundConfig = this.repository.findById(serviceA.getId());
        final Optional<Service> foundEureka = this.repository.findById(serviceB.getId());

        Assert.assertTrue(foundConfig.isPresent());
        Assert.assertTrue(foundEureka.isPresent());

        Assert.assertEquals(foundConfig.get(), serviceA);
        Assert.assertEquals(foundEureka.get(), serviceB);
    }

    @Test
    public void testFindByNameAndInstanceCount() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = repository.findByNameAndInstanceCount(NAME_B, COUNT_B);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), serviceB);
        Assert.assertTrue(repository.findByNameAndInstanceCount(NAME_B, COUNT_A).isEmpty());
    }

    @Test
    public void testFindByNameAndInstanceCountAndType() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = repository.findByNameAndInstanceCountAndType(NAME_B, COUNT_B, BACK_END);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), serviceB);
        Assert.assertTrue(repository.findByNameAndInstanceCountAndType(NAME_B, COUNT_A, ServiceType.BOTH).isEmpty());
    }

    @Test
    public void testFindByNameOrInstanceCount() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = Arrays.asList(serviceA, serviceB);
        List<Service> foundServices = repository.findByNameOrInstanceCount(NAME_A, COUNT_B);

        services.sort(Comparator.comparing(Service::getId));
        foundServices.sort(Comparator.comparing(Service::getId));

        Assert.assertEquals(foundServices.size(), 2);
        Assert.assertEquals(foundServices, services);

        foundServices = repository.findByNameOrInstanceCount("fake-name", COUNT_A);

        Assert.assertEquals(foundServices.size(), 1);
        Assert.assertEquals(foundServices.get(0), serviceA);
    }

    @Test
    public void testFindByNameAndIsActiveOrProperties() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = Arrays.asList(serviceA, serviceB);
        List<Service> foundServices = repository.findByNameAndIsActiveOrProperties(NAME_A, true, PROPERTIES_B);

        services.sort(Comparator.comparing(Service::getId));
        foundServices.sort(Comparator.comparing(Service::getId));

        Assert.assertEquals(foundServices.size(), 2);
        Assert.assertEquals(foundServices, services);

        foundServices = repository.findByNameAndIsActiveOrProperties(NAME_B, false, new HashMap<>());
        Assert.assertEquals(foundServices.size(), 1);
        Assert.assertEquals(foundServices.get(0), serviceB);
    }
}

