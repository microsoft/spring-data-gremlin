/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository;

import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.common.domain.TestDomain;
import com.microsoft.spring.data.gremlin.common.repository.TestDomainRepository;
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
public class TestDomainRepositoryIT {

    private static final String NAME = "incarnation";
    private static final int LEVEL = 4;
    private static final TestDomain DOMAIN = new TestDomain(NAME, LEVEL);

    @Autowired
    private TestDomainRepository repository;

    @Before
    public void setup() {
        this.repository.deleteAll();
    }

    @Test
    public void testDomainWithNoIdName() {
        Assert.assertEquals(0, this.repository.vertexCount());
        Assert.assertFalse(this.repository.findById(NAME).isPresent());

        this.repository.save(DOMAIN);
        final Optional<TestDomain> optional = this.repository.findById(NAME);

        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(DOMAIN.getName(), optional.get().getName());
        Assert.assertEquals(DOMAIN.getLevel(), optional.get().getLevel());

        this.repository.deleteById(NAME);
        Assert.assertFalse(this.repository.findById(NAME).isPresent());
    }
}
