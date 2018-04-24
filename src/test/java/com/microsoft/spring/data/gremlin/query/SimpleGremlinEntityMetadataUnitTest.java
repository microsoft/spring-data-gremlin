/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query;

import com.microsoft.spring.data.gremlin.common.domain.Person;
import org.junit.Assert;
import org.junit.Test;

public class SimpleGremlinEntityMetadataUnitTest {

    @Test
    public void testSimpleGremlinEntityMetadata() {
        final SimpleGremlinEntityMetadata<Person> metadata = new SimpleGremlinEntityMetadata<>(Person.class);

        Assert.assertNotNull(metadata);
        Assert.assertEquals(metadata.getJavaType(), Person.class);
    }
}
