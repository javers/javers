package org.javers.model.mapping;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class EntityManager {
    private EntityFactory entityFactory;
    private Map<Class<?>, Entity> managedEntities = new HashMap<>();

    public EntityManager(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    public Entity getByClass(Class<?> forClass) {
        if (!isManaged(forClass)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, forClass.getName());
        }

        return managedEntities.get(forClass);
    }

    public void manage(Class<?> classToManage) {
        Entity entity = entityFactory.create(classToManage);
        managedEntities.put(classToManage, entity);
    }

    public boolean isManaged(Class<?> forClass) {
        return managedEntities.containsKey(forClass);
    }
}
