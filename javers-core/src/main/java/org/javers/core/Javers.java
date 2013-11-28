package org.javers.core;

import org.javers.core.diff.DiffFactory;
import org.javers.model.domain.Diff;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.object.graph.ObjectGraphBuilder;

/**
 * Facade to JaVers instance.
 * Should be constructed by {@link JaversBuilder} provided with your domain model metadata and configuration.
 * <br/>
 *
 * Domain TODO: move to doc
 * <ul>
 *   <li>Entity - a class in client's domain model. List of those classes should be provided to JaversBuilder</li>
 *   <li>CDO - client's domain object, instance of an Entity</li>
 * </ul>
 *
 * @author bartosz walacik
 */
public class Javers {

    private EntityManager entityManager;

    private DiffFactory diffFactory;

    private ObjectGraphBuilder objectGraphBuilder;

    /**
     * JaVers instance should be constructed by {@link JaversBuilder}
     */
    public Javers(EntityManager entityManager, DiffFactory diffFactory, ObjectGraphBuilder objectGraphBuilder) {
        this.entityManager = entityManager;
        this.diffFactory = diffFactory;
        this.objectGraphBuilder = objectGraphBuilder;
    }

    public ManagedClass getByClass(Class<?> forClass) {
        return entityManager.getByClass(forClass);
    }

    public boolean isManaged(Class<?> forClass) {
        return entityManager.isManaged(forClass);
    }

    public Diff compare(String user, Object left, Object right) {
        return diffFactory.create(user, objectGraphBuilder.buildGraph(left), objectGraphBuilder.buildGraph(right));
    }
}
