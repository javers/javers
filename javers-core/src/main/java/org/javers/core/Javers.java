package org.javers.core;

import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityManager;

/**
 * Facade to JaVers instance.
 * Should be constructed by {@link JaversFactory} provided with your domain model metadata and configuration.
 * <br/>
 *
 * Domain TODO: move to doc
 * <ul>
 *   <li>Entity - a class in client's domain model. List of those classes should be provided to JaversFactory</li>
 *   <li>CDO - client's domain object, instance of an Entity</li>
 * </ul>
 *
 * @author bartosz walacik
 */
public class Javers {

    EntityManager entityManager;

    protected Javers(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected void manage(Class<?> managedClass) {
        entityManager.manage(managedClass);
    }

    public Entity getByClass(Class<?> forClass) {
        return entityManager.getByClass(forClass);
    }

    public boolean isManaged(Class<?> forClass) {
        return entityManager.isManaged(forClass);
    }
}
