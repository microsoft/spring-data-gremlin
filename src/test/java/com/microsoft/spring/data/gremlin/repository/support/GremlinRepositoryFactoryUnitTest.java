/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.support;

import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.query.GremlinOperations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.query.ExtensionAwareEvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
public class GremlinRepositoryFactoryUnitTest {

    @Mock
    private GremlinOperations operations;

    @Autowired
    private ApplicationContext context;

    private GremlinRepositoryFactory factory;

    @Before
    public void setup() {
        this.factory = new GremlinRepositoryFactory(this.operations, this.context);
    }

    @Test
    public void testGetRepositoryBaseClass() {
        Assert.assertEquals(SimpleGremlinRepository.class, this.factory.getRepositoryBaseClass(null));
    }

    @Test
    public void testGetEntityInformation() {
        final EntityInformation<Person, String> information = this.factory.getEntityInformation(Person.class);

        Assert.assertNotNull(information);
        Assert.assertEquals(information.getIdType(), String.class);
    }

    @Test
    public void testGetQueryLookupStrategy() {
        final Optional<QueryLookupStrategy> strategyOptional = this.factory.getQueryLookupStrategy(
                QueryLookupStrategy.Key.CREATE, new ExtensionAwareEvaluationContextProvider());

        Assert.assertTrue(strategyOptional.isPresent());
    }
}
