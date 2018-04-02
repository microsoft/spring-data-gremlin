/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.config;

import com.microsoft.spring.data.gremlin.common.Constants;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(GremlinRepositoryRegistrar.class)
public @interface EnableGremlinRepository {

    /**
     * Alias for basePackages.
     */
    String[] value() default {};

    /**
     * Base packages to scan for components with annotations.
     */
    String[] basePackages() default {};

    /**
     * Type-safe version of basePackages
     */
    Class<?>[] basePackagesClasses() default {};

    /**
     * Specifies types for component scan.
     */
    Filter[] includeFilters() default {};

    /**
     * Specifies types for skipping component scan.
     */
    Filter[] excludeFilters() default {};

    /**
     * Specifics the postfix to be used for custom repository implementation class name.
     */
    String repositoryImplementationPostFix() default Constants.DEFAULT_REPOSITORY_IMPLEMENT_POSTFIX;

    /**
     * Configures the repository base class to be used to create repository.
     */
    Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    /**
     * Configures whether nested repository interface.
     */
    boolean considerNestedRepositories() default false;
}

