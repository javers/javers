package org.javers.core;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityManager;

/**
 * Facade to JaVers instance.
 * Should be constructed by {@link JaversFactory} provided with your domain model metadata and configuration.
 *
 * @author bartosz walacik
 */
public class Javers {

    EntityManager entityManager = new EntityManager();

    protected Javers() {
    }

    public Entity getByClass(Class<?> forClass) {
        return entityManager.getByClass(forClass);
    }

    public void manage(Class<?> managedClass) {
        entityManager.manage(managedClass);
    }

    public boolean isManaged(Class<?> forClass) {
        return entityManager.isManaged(forClass);
    }
}
