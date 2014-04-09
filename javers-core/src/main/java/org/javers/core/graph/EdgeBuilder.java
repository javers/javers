package org.javers.core.graph;

import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author bartosz walacik
 */
class EdgeBuilder {
    private final TypeMapper typeMapper;

    EdgeBuilder(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }
}
