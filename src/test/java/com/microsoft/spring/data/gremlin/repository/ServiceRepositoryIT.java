/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository;

import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.common.domain.Service;
import com.microsoft.spring.data.gremlin.common.repository.ServiceRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
public class ServiceRepositoryIT {

    private final String configId = "1234";
    private final String eurekaId = "8731";

    private final int configCount = 2;
    private final int eurekaCount = 8;

    private final String configName = "cloud-config";
    private final String eurekaName = "eureka-server";

    private final Service config = new Service(configId, configCount, true, configName);
    private final Service eureka = new Service(eurekaId, eurekaCount, false, eurekaName);

    @Autowired
    private ServiceRepository repository;

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
}
