package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * EntityManager bootstrap is two-phased:
 * <ol>
 *     <li/>JaVers bootstrap should
 *          registering client's Entities and ValueObjects through {@link #registerEntity(Class)}
 *          and {@link #registerValueObject(Class)}. <br/>
 *          In this phase, EntityManager creates proper {@link JaversType}'s
 *          in {@link TypeMapper}.
 *     <li/>When all types are registered, JaVers bootstrap calls {@link #buildManagedClasses()},
 *          in order to create Entities and ValueObjects for all previously registered types.
 * </ol>
 *
 * @author bartosz walacik
 */
public class EntityManager {

    private EntityFactory entityFactory;

    private Map<Class<?>, Entity> managedEntities = new HashMap<>();

    public EntityManager(EntityFactory entityFactory) {
        Validate.argumentIsNotNull(entityFactory);

        this.entityFactory = entityFactory;
    }

    /**
     * @throws JaversException if class is not managed
     */
    public Entity getByClass(Class<?> forClass) {
        if (!isRegisterd(forClass)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, forClass.getName());
        }
        if (isRegisterd(forClass) && !isManaged(forClass)) {
            throw new JaversException(JaversExceptionCode.ENTITY_MANAGER_NOT_INITIALIZED, forClass.getName());
        }
        return managedEntities.get(forClass);
    }

    public void registerEntity(Class<?> classToManage) {
        Validate.argumentIsNotNull(classToManage);

        if (isRegisterd(classToManage)) {
            return; //already managed
        }

        entityFactory.typeMapper.registerReferenceType(classToManage);
    }

    public void registerValueObject(Class<?> classToManage) {
        Validate.argumentIsNotNull(classToManage);

        if (isRegisterd(classToManage)) {
            return; //already managed
        }

        entityFactory.typeMapper.registerValueObjectType(classToManage);
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

        for (Class referenceClass : entityFactory.typeMapper.getReferenceTypes()) {
            Entity entity = entityFactory.createEntity(referenceClass);
            managedEntities.put(referenceClass, entity);
        }
    }
}
