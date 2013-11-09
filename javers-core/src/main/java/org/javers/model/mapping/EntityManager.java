package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.EntityReferenceType;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.mapping.type.ValueObjectType;

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

    private final EntityFactory entityFactory;
    private final ValueObjectFactory valueObjectFactory;
    private final TypeMapper typeMapper;

    private ManagedClasses managedClasses = new ManagedClasses();

    public EntityManager(EntityFactory entityFactory, ValueObjectFactory valueObjectFactory, TypeMapper typeMapper) {
        Validate.argumentIsNotNull(entityFactory);
        Validate.argumentIsNotNull(valueObjectFactory);
        Validate.argumentIsNotNull(typeMapper);

        this.entityFactory = entityFactory;
        this.typeMapper = typeMapper;
        this.valueObjectFactory = valueObjectFactory;
    }

    /**
     * @throws JaversException if class is not managed
     */
    public ManagedClass getByClass(Class<?> clazz) {
        if (!isRegisterd(clazz)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, clazz.getName());
        }
        if (isRegisterd(clazz) && !isManaged(clazz)) {
            throw new JaversException(JaversExceptionCode.ENTITY_MANAGER_NOT_INITIALIZED, clazz.getName());
        }
        return managedClasses.getBySourceClass(clazz);
    }

    public void registerEntity(Class<?> entityClass) {
        Validate.argumentIsNotNull(entityClass);

        if (isRegisterd(entityClass)) {
            return; //already managed
        }

        typeMapper.registerEntityReferenceType(entityClass);
    }

    public void registerValueObject(Class<?> valueObjectClass) {
        Validate.argumentIsNotNull(valueObjectClass);

        if (isRegisterd(valueObjectClass)) {
            return; //already managed
        }

        typeMapper.registerValueObjectType(valueObjectClass);
    }

    private boolean isRegisterd(Class<?> managedClass) {
        return typeMapper.isMapped(managedClass);
    }

    public boolean isManaged(Class<?> clazz) {
        return managedClasses.containsManagedClassWithSourceClass(clazz);
    }

    /**
     * EntityManager is up & ready after calling {@link #buildManagedClasses()}
     */
    public boolean isInitialized() {
        return managedClasses.count() == typeMapper.getCountOfEntitiesAndValueObjects();
    }

    /**
     * call that if all Entities and ValueObject are registered
     */
    public void buildManagedClasses() {
        for (EntityReferenceType refType : typeMapper.getMappedEntityReferenceTypes()) {
            manage(refType.getBaseJavaType());
        }
        for (ValueObjectType voType : typeMapper.getMappedValueObjectTypes()) {
            manage(voType.getBaseJavaType());
        }
    }

    private void manage(Class classToManage) {
        ManagedClassFactory managedClassFactory = selectFactoryAccording(classToManage);
        ManagedClass managedClass = managedClassFactory.create(classToManage);
        managedClasses.add(managedClass);
    }

    private ManagedClassFactory selectFactoryAccording(Class classToManage) {
        JaversType javersType = typeMapper.getJavesrType(classToManage);
        if (javersType instanceof EntityReferenceType) {
            return entityFactory;
        } else if (javersType instanceof ValueObjectType){
            return valueObjectFactory;
        }
        throw new IllegalArgumentException("Only EntityReferenceType or ValueObjectType is a legal argument");
    }
}
