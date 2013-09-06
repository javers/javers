package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.EntityReferenceType;

import java.util.HashMap;
import java.util.List;
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

    /**
     * @throws JaversException if class is not managed
     */
    public Entity getByClass(Class<?> forClass) {
        if (!isRegisterd(forClass)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, forClass.getName());
        }
        if(isRegisterd(forClass) && !isManaged(forClass)){
            throw new JaversException(JaversExceptionCode.ENTITY_MANAGER_NOT_INITIALIZED, forClass.getName());
        }
        return managedEntities.get(forClass);
    }

    public void registerEntity(Class<?> classToManage) {
        Validate.argumentIsNotNull(classToManage);

        if (isRegisterd(classToManage)){
            return; //already managed
        }

        entityFactory.typeMapper.registerReferenceType(classToManage);
    }

    public void registerValueObject(Class<?> classToManage) {
        Validate.argumentIsNotNull(classToManage);
        entityFactory.typeMapper.registerObjectValueType(classToManage);
    }

    private boolean isRegisterd(Class<?> managedClass) {
        return entityFactory.typeMapper.isMapped(managedClass);
    }

    public boolean isManaged(Class<?> managedClass) {
        return managedEntities.containsKey(managedClass);
    }

    /**
     * call that if all Entities and ValueObject are registered
     */
    public void buildManagedClasses() {

        for(Class referenceClass : entityFactory.typeMapper.getReferenceTypes()) {
            Entity entity = entityFactory.createEntity(referenceClass);
            managedEntities.put(referenceClass, entity);
        }
    }
}
