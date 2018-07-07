/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common.domain;

import com.microsoft.spring.data.gremlin.annotation.Vertex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Vertex
@Getter
@Setter
@NoArgsConstructor
public class AdvancedUser extends User {

    private int level;

    public AdvancedUser(String id, String name, int level) {
        super(id, name);

        this.level = level;
    }
}
