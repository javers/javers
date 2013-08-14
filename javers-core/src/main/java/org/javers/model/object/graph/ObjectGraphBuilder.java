package org.javers.model.object.graph;

import org.javers.model.mapping.EntityManager;

/**
 * Creates graph based on ObjectWrappers
 *
 * @author bartosz walacik
 */
public class ObjectGraphBuilder {
    protected final EntityManager entityManager;

    public ObjectGraphBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * @param rootCdo client's domain object, it should be root of an aggregate, tree root
     *                or any node in objects graph from all other nodes are navigable
     * @return graph node, typically root
     */
    public ObjectNode build(Object rootCdo) {
        return null;
    }
}