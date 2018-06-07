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

import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public class ServiceRepositoryIT {

    private static Service SERVICE_A;
    private static Service SERVICE_B;

    private static Date CREATE_DATE_A;
    private static Date CREATE_DATE_B;

    private static final Map<String, Object> PROPERTIES_A = new HashMap<>();
    private static final Map<String, Object> PROPERTIES_B = new HashMap<>();

    private static final String ID_A = "1234";
    private static final String ID_B = "8731";

    private static final String NAME_A = "SERVICE_A-name";
    private static final String NAME_B = "SERVICE_B-name";

    private static final int COUNT_A = 2;
    private static final int COUNT_B = 8;

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private SimpleDependencyRepository dependencyRepo;

    @BeforeClass
    @SneakyThrows
    public static void initialize() {
        PROPERTIES_B.put("SERVICE_B-port", 8761);
        PROPERTIES_B.put("priority", "high");
        PROPERTIES_B.put("enabled-hystrix", false);

        PROPERTIES_A.put("SERVICE_A-port", 8888);
        PROPERTIES_A.put("SERVICE_B-port", 8761);
        PROPERTIES_A.put("priority", "highest");

        CREATE_DATE_A = new SimpleDateFormat("yyyyMMdd").parse("20180601");
        CREATE_DATE_B = new SimpleDateFormat("yyyyMMdd").parse("20180603");

        SERVICE_A = new Service(ID_A, COUNT_A, true, NAME_A, ServiceType.FRONT_END,
                CREATE_DATE_A, PROPERTIES_A);
        SERVICE_B = new Service(ID_B, COUNT_B, false, NAME_B, ServiceType.BACK_END,
                CREATE_DATE_B, PROPERTIES_B);
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
        Assert.assertFalse(this.repository.findById(SERVICE_A.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(SERVICE_B.getId()).isPresent());

        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        Optional<Service> foundOptional = this.repository.findById(SERVICE_A.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), SERVICE_A);

        foundOptional = this.repository.findById(SERVICE_B.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), SERVICE_B);

        this.repository.deleteById(SERVICE_A.getId());
        this.repository.deleteById(SERVICE_B.getId());

        Assert.assertFalse(this.repository.findById(SERVICE_A.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(SERVICE_B.getId()).isPresent());
    }

    @Test
    public void testEdgeFromToStringId() {
        final SimpleDependency depend = new SimpleDependency("fakeId", "fakeName", SERVICE_A.getId(), SERVICE_B.getId());

        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);
        this.dependencyRepo.save(depend);

        final Optional<SimpleDependency> foundOptional = this.dependencyRepo.findById(depend.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), depend);

        this.dependencyRepo.delete(foundOptional.get());
        Assert.assertTrue(this.repository.findById(SERVICE_A.getId()).isPresent());
        Assert.assertTrue(this.repository.findById(SERVICE_B.getId()).isPresent());
    }

    @Test
    public void testFindByName() {
        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        final List<Service> services = this.repository.findByName(SERVICE_A.getName());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), SERVICE_A);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByName(SERVICE_A.getName()).isEmpty());
    }

    @Test
    public void testFindByInstanceCount() {
        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        final List<Service> services = this.repository.findByInstanceCount(SERVICE_B.getInstanceCount());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), SERVICE_B);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByInstanceCount(SERVICE_B.getInstanceCount()).isEmpty());
    }

    @Test
    public void testFindByIsActive() {
        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        final List<Service> services = this.repository.findByIsActive(SERVICE_B.isActive());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), SERVICE_B);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByIsActive(SERVICE_B.isActive()).isEmpty());
    }

    @Test
    public void testFindByProperties() {
        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        final List<Service> services = this.repository.findByProperties(SERVICE_B.getProperties());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), SERVICE_B);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByProperties(SERVICE_B.getProperties()).isEmpty());
    }

    @Test
    public void testFindById() {
        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        final Optional<Service> foundConfig = this.repository.findById(SERVICE_A.getId());
        final Optional<Service> foundEureka = this.repository.findById(SERVICE_B.getId());

        Assert.assertTrue(foundConfig.isPresent());
        Assert.assertTrue(foundEureka.isPresent());

        Assert.assertEquals(foundConfig.get(), SERVICE_A);
        Assert.assertEquals(foundEureka.get(), SERVICE_B);
    }

    @Test
    public void testFindByNameAndInstanceCount() {
        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        final List<Service> services = repository.findByNameAndInstanceCount(NAME_B, COUNT_B);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), SERVICE_B);
        Assert.assertTrue(repository.findByNameAndInstanceCount(NAME_B, COUNT_A).isEmpty());
    }

    @Test
    public void testFindByNameAndInstanceCountAndType() {
        this.repository.save(SERVICE_A);
        this.repository.save(SERVICE_B);

        final List<Service> services = repository.findByNameAndInstanceCountAndType(NAME_B, COUNT_B,
                ServiceType.BACK_END);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), SERVICE_B);
        Assert.assertTrue(repository.findByNameAndInstanceCountAndType(NAME_B, COUNT_A, ServiceType.BOTH)
                .isEmpty());
    }

    @Test
    public void testFindByNameOrInstanceCount() {
        final List<Service> services = Arrays.asList(SERVICE_A, SERVICE_B);

        this.repository.saveAll(services);

        List<Service> foundServices = repository.findByNameOrInstanceCount(NAME_A, COUNT_B);

        services.sort(Comparator.comparing(Service::getId));
        foundServices.sort(Comparator.comparing(Service::getId));

        Assert.assertEquals(foundServices.size(), 2);
        Assert.assertEquals(foundServices, services);

        foundServices = repository.findByNameOrInstanceCount("fake-name", COUNT_A);

        Assert.assertEquals(foundServices.size(), 1);
        Assert.assertEquals(foundServices.get(0), SERVICE_A);
    }

    @Test
    public void testFindByNameAndIsActiveOrProperties() {
        final List<Service> services = Arrays.asList(SERVICE_A, SERVICE_B);

        this.repository.saveAll(services);

        List<Service> foundServices = repository.findByNameAndIsActiveOrProperties(NAME_A, true,
                PROPERTIES_B);

        services.sort(Comparator.comparing(Service::getId));
        foundServices.sort(Comparator.comparing(Service::getId));

        Assert.assertEquals(foundServices.size(), 2);
        Assert.assertEquals(foundServices, services);

        foundServices = repository.findByNameAndIsActiveOrProperties(NAME_B, false, new HashMap<>());
        Assert.assertEquals(foundServices.size(), 1);
        Assert.assertEquals(foundServices.get(0), SERVICE_B);
    }
}

