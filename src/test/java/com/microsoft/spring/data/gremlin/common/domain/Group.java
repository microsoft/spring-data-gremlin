/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common.domain;

import com.microsoft.spring.data.gremlin.annotation.Edge;
import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.annotation.GeneratedValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Edge
@Data
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue
    private Long id;

    @EdgeFrom
    private Student student;

    @EdgeTo
    private GroupOwner groupOwner;

    public Group(Student student, GroupOwner groupOwner) {
        this.student = student;
        this.groupOwner = groupOwner;
    }
}
