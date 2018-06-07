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
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public class ServiceRepositoryIT {

    private static final Map<String, Object> serviceAProperties = new HashMap<>();
    private static final Map<String, Object> serviceBProperties = new HashMap<>();

    private final String serviceAId = "1234";
    private final String serviceBId = "8731";

    private final int serviceACount = 2;
    private final int serviceBCount = 8;

    private final String serviceAName = "serviceA-name";
    private final String serviceBName = "serviceB-name";

    private final Service serviceA = new Service(serviceAId, serviceACount, true, serviceAName,
            ServiceType.FRONT_END, serviceAProperties);
    private final Service serviceB = new Service(serviceBId, serviceBCount, false, serviceBName,
            ServiceType.BACK_END, serviceBProperties);

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private SimpleDependencyRepository dependencyRepo;

    @BeforeClass
    public static void initialize() {
        serviceBProperties.put("serviceB-port", 8761);
        serviceBProperties.put("priority", "high");
        serviceBProperties.put("enabled-hystrix", false);

        serviceAProperties.put("serviceA-port", 8888);
        serviceAProperties.put("serviceB-port", 8761);
        serviceAProperties.put("priority", "highest");
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
    public void testServiceQueries() {
        Assert.assertFalse(this.repository.findById(this.serviceA.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(this.serviceB.getId()).isPresent());

        this.repository.save(serviceA);
        this.repository.save(serviceB);

        Optional<Service> foundOptional = this.repository.findById(this.serviceA.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), this.serviceA);

        foundOptional = this.repository.findById(this.serviceB.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), this.serviceB);

        this.repository.deleteById(this.serviceA.getId());
        this.repository.deleteById(this.serviceB.getId());

        Assert.assertFalse(this.repository.findById(this.serviceA.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(this.serviceB.getId()).isPresent());
    }

    @Test
    public void testEdgeFromToStringId() {
        final SimpleDependency depend = new SimpleDependency("fakeId", "fakeName", serviceA.getId(), serviceB.getId());

        this.repository.save(serviceA);
        this.repository.save(serviceB);
        this.dependencyRepo.save(depend);

        final Optional<SimpleDependency> foundOptional = this.dependencyRepo.findById(depend.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), depend);

        this.dependencyRepo.delete(foundOptional.get());
        Assert.assertTrue(this.repository.findById(this.serviceA.getId()).isPresent());
        Assert.assertTrue(this.repository.findById(this.serviceB.getId()).isPresent());
    }

    @Test
    public void testServiceFindByName() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByName(this.serviceA.getName());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.serviceA);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByName(this.serviceA.getName()).isEmpty());
    }

    @Test
    public void testServiceFindByInstanceCount() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByInstanceCount(this.serviceB.getInstanceCount());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.serviceB);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByInstanceCount(this.serviceB.getInstanceCount()).isEmpty());
    }

    @Test
    public void testServiceFindByIsActive() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByIsActive(this.serviceB.isActive());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.serviceB);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByIsActive(this.serviceB.isActive()).isEmpty());
    }

    @Test
    public void testServiceFindByProperties() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = this.repository.findByProperties(this.serviceB.getProperties());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.serviceB);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByProperties(this.serviceB.getProperties()).isEmpty());
    }

    @Test
    public void testServiceFindById() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final Optional<Service> foundConfig = this.repository.findById(this.serviceA.getId());
        final Optional<Service> foundEureka = this.repository.findById(this.serviceB.getId());

        Assert.assertTrue(foundConfig.isPresent());
        Assert.assertTrue(foundEureka.isPresent());

        Assert.assertEquals(foundConfig.get(), this.serviceA);
        Assert.assertEquals(foundEureka.get(), this.serviceB);
    }

    @Test
    public void testServiceFindByNameAndInstanceCount() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = repository.findByNameAndInstanceCount(serviceBName, serviceBCount);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.serviceB);
        Assert.assertTrue(repository.findByNameAndInstanceCount(serviceBName, serviceACount).isEmpty());
    }

    @Test
    public void testServiceFindByNameAndInstanceCountAndType() {
        this.repository.save(serviceA);
        this.repository.save(serviceB);

        final List<Service> services = repository.findByNameAndInstanceCountAndType(serviceBName, serviceBCount,
                ServiceType.BACK_END);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.serviceB);
        Assert.assertTrue(repository.findByNameAndInstanceCountAndType(serviceBName, serviceACount, ServiceType.BOTH)
                .isEmpty());
    }

    @Test
    public void testServiceFindByNameAndInstanceCount() {
        this.repository.save(config);
        this.repository.save(eureka);

        final List<Service> services = repository.findByNameAndInstanceCount(eurekaName, eurekaCount);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.eureka);
        Assert.assertTrue(repository.findByNameAndInstanceCount(eurekaName, configCount).isEmpty());
    }

    @Test
    public void testServiceFindByNameAndInstanceCountAndType() {
        this.repository.save(config);
        this.repository.save(eureka);

        final List<Service> services = repository.findByNameAndInstanceCountAndType(eurekaName, eurekaCount,
                ServiceType.BACK_END);

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.eureka);
        Assert.assertTrue(repository.findByNameAndInstanceCountAndType(eurekaName, configCount, ServiceType.BOTH)
                .isEmpty());
    }
}

