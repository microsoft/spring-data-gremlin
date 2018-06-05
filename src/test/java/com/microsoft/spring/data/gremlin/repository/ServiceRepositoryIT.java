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

    private static final Map<String, Object> configProperties = new HashMap<>();
    private static final Map<String, Object> eurekaProperties = new HashMap<>();

    private final String configId = "1234";
    private final String eurekaId = "8731";

    private final int configCount = 2;
    private final int eurekaCount = 8;

    private final String configName = "cloud-config";
    private final String eurekaName = "eureka-server";

    private final Service config = new Service(configId, configCount, true, configName, ServiceType.FRONT_END,
            configProperties);
    private final Service eureka = new Service(eurekaId, eurekaCount, false, eurekaName, ServiceType.BACK_END,
            eurekaProperties);

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private SimpleDependencyRepository dependencyRepo;

    @BeforeClass
    public static void initialize() {
        eurekaProperties.put("eureka-port", 8761);
        eurekaProperties.put("priority", "high");
        eurekaProperties.put("enabled-hystrix", false);

        configProperties.put("config-port", 8888);
        configProperties.put("eureka-port", 8761);
        configProperties.put("priority", "highest");
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
        Assert.assertFalse(this.repository.findById(this.config.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(this.eureka.getId()).isPresent());

        this.repository.save(config);
        this.repository.save(eureka);

        Optional<Service> foundOptional = this.repository.findById(this.config.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), this.config);

        foundOptional = this.repository.findById(this.eureka.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), this.eureka);

        this.repository.deleteById(this.config.getId());
        this.repository.deleteById(this.eureka.getId());

        Assert.assertFalse(this.repository.findById(this.config.getId()).isPresent());
        Assert.assertFalse(this.repository.findById(this.eureka.getId()).isPresent());
    }

    @Test
    public void testEdgeFromToStringId() {
        final SimpleDependency depend = new SimpleDependency("fake-id", "fake-name", config.getId(), eureka.getId());

        this.repository.save(config);
        this.repository.save(eureka);
        this.dependencyRepo.save(depend);

        final Optional<SimpleDependency> foundOptional = this.dependencyRepo.findById(depend.getId());
        Assert.assertTrue(foundOptional.isPresent());
        Assert.assertEquals(foundOptional.get(), depend);

        this.dependencyRepo.delete(foundOptional.get());
        Assert.assertTrue(this.repository.findById(this.config.getId()).isPresent());
        Assert.assertTrue(this.repository.findById(this.eureka.getId()).isPresent());
    }

    @Test
    public void testServiceFindByName() {
        this.repository.save(config);
        this.repository.save(eureka);

        final List<Service> services = this.repository.findByName(this.config.getName());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.config);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByName(this.config.getName()).isEmpty());
    }

    @Test
    public void testServiceFindByInstanceCount() {
        this.repository.save(config);
        this.repository.save(eureka);

        final List<Service> services = this.repository.findByInstanceCount(this.eureka.getInstanceCount());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.eureka);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByInstanceCount(this.eureka.getInstanceCount()).isEmpty());
    }

    @Test
    public void testServiceFindByIsActive() {
        this.repository.save(config);
        this.repository.save(eureka);

        final List<Service> services = this.repository.findByIsActive(this.eureka.isActive());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.eureka);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByIsActive(this.eureka.isActive()).isEmpty());
    }

    @Test
    public void testServiceFindByProperties() {
        this.repository.save(config);
        this.repository.save(eureka);

        final List<Service> services = this.repository.findByProperties(this.eureka.getProperties());

        Assert.assertEquals(services.size(), 1);
        Assert.assertEquals(services.get(0), this.eureka);

        this.repository.deleteAll();

        Assert.assertTrue(this.repository.findByProperties(this.eureka.getProperties()).isEmpty());
    }

    @Test
    public void testServiceFindById() {
        this.repository.save(config);
        this.repository.save(eureka);

        final Optional<Service> foundConfig = this.repository.findById(this.config.getId());
        final Optional<Service> foundEureka = this.repository.findById(this.eureka.getId());

        Assert.assertTrue(foundConfig.isPresent());
        Assert.assertTrue(foundEureka.isPresent());

        Assert.assertEquals(foundConfig.get(), this.config);
        Assert.assertEquals(foundEureka.get(), this.eureka);
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

