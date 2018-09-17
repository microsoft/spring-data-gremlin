/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common.repository;

import com.microsoft.spring.data.gremlin.common.domain.Book;
import com.microsoft.spring.data.gremlin.repository.GremlinRepository;

import java.util.List;

public interface BookRepository extends GremlinRepository<Book, Integer> {

    List<Book> findByNameOrPrice(String name, Double price);
}
