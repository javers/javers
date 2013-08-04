package org.javers.model.mapping;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class EntityManager {
    private EntityFactory entityFactory = new EntityFactory();
    private Map<Class<?>, Entity> managedEntities = new HashMap<>();

    public Entity getByClass(Class<?> forClass) {
        if (!isManaged(forClass)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, forClass.getSimpleName());
        }

        return managedEntities.get(forClass);
    }

    public void manage(Class<?> classToManage) {
        Entity entity = entityFactory.createFromBean(classToManage);
        managedEntities.put(classToManage, entity);
    }

    public boolean isManaged(Class<?> forClass) {
        return managedEntities.containsKey(forClass);
    }
}
