package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.ReferenceType;
import org.javers.model.mapping.type.TypeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class EntityManager {
    private EntityFactory entityFactory;
    //private TypeMapper typeMapper;
    private Map<Class<?>, Entity> managedEntities = new HashMap<>();

    public EntityManager(EntityFactory entityFactory) {
        //Validate.argumentIsNotNull(typeMapper);
        Validate.argumentIsNotNull(entityFactory);

        this.entityFactory = entityFactory;
        //this.typeMapper = typeMapper;
    }

    public Entity getByClass(Class<?> forClass) {
        if (!isManaged(forClass)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, forClass.getName());
        }

        return managedEntities.get(forClass);
    }

    public void manage(Class<?> classToManage) {
        Validate.argumentIsNotNull(classToManage);

        if (managedEntities.containsKey(classToManage)){
            return; //already managed
        }

        Entity entity = entityFactory.create(classToManage);
        managedEntities.put(classToManage, entity);
    }

    public boolean isManaged(Class<?> forClass) {
        return managedEntities.containsKey(forClass);
    }
}
