/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository;

import com.microsoft.spring.data.gremlin.common.TestRepositoryConfiguration;
import com.microsoft.spring.data.gremlin.common.domain.UserDomain;
import com.microsoft.spring.data.gremlin.common.repository.UserDomainRepository;
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
public class UserDomainRepositoryIT {

    private static final String NAME = "incarnation";
    private static final int LEVEL = 4;
    private static final UserDomain DOMAIN = new UserDomain(NAME, LEVEL);

    @Autowired
    private UserDomainRepository repository;

    @Before
    public void setup() {
        this.repository.deleteAll();
    }

    @Test
    public void testDomainWithNoIdName() {
        Assert.assertEquals(0, this.repository.vertexCount());
        Assert.assertFalse(this.repository.findById(NAME).isPresent());

        this.repository.save(DOMAIN);
        final Optional<UserDomain> optional = this.repository.findById(NAME);

        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(DOMAIN.getName(), optional.get().getName());
        Assert.assertEquals(DOMAIN.getLevel(), optional.get().getLevel());

        this.repository.deleteById(NAME);
        Assert.assertFalse(this.repository.findById(NAME).isPresent());
    }
}
