/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.exception.UnexpectedGremlinSourceTypeException;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class GremlinScriptVertexLiteral extends GremlinScriptPropertiesLiteral implements GremlinScript<String> {

    @Override
    public String generateScript(@NonNull GremlinSource source) {
        if (source instanceof GremlinSourceVertex) {
            throw new UnexpectedGremlinSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();
        final String label = source.getLabel();
        final String id = source.getId();
        final Map<String, Object> properties = source.getProperties();

        Assert.notNull(label, "label should not be null");
        Assert.notNull(id, "id should not be null");
        Assert.notNull(properties, "properties should not be null");

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);

        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_ADD_VERTEX, label));
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_PROPERTY_STRING, Constants.PROPERTY_ID, id));

        super.generateGremlinScriptProperties(scriptList, properties);

        return String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);
    }
}
