package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

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

    private ManagedClasses managedEntities = new ManagedClasses();

    public EntityManager(EntityFactory entityFactory, ValueObjectFactory valueObjectFactory, TypeMapper typeMapper) {
        //TODO troche glupio to wyglada, na chwile obecna nie wiem co z tym zrobic (czy w ogole cos robic)
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
        return managedEntities.getBySourceClass(clazz);
    }

    public void registerEntity(Class<?> classToManage) {
        Validate.argumentIsNotNull(classToManage);

        if (isRegisterd(classToManage)) {
            return; //already managed
        }

        typeMapper.registerEntityReferenceType(classToManage);
    }

    public void registerValueObject(Class<?> classToManage) {
        Validate.argumentIsNotNull(classToManage);

        if (isRegisterd(classToManage)) {
            return; //already managed
        }

        typeMapper.registerValueObjectType(classToManage);
    }

    private boolean isRegisterd(Class<?> managedClass) {
        return typeMapper.isMapped(managedClass);
    }

    public boolean isManaged(Class<?> clazz) {
        return managedEntities.containsManagedClassWithSourceClass(clazz);
    }

    /**
     * call that if all Entities and ValueObject are registered
     */
    public void buildManagedClasses() {
        for (Class referenceClass : typeMapper.getReferenceTypes()) {
            manage(referenceClass);
        }
    }

    private void manage(Class referenceClass) {
        ManagedClassFactory managedClassFactory = selectFactoryAccording(referenceClass);
        ManagedClass managedClass = managedClassFactory.create(referenceClass);
        managedEntities.add(managedClass);
    }

    private ManagedClassFactory selectFactoryAccording(Class referenceClass) {
        JaversType javersType = typeMapper.getJavesrType(referenceClass);
        if (javersType.isEntityReferenceType()) {
            return entityFactory;
        } else if (javersType.isValueObject()){
            return valueObjectFactory;
        }
        throw new IllegalArgumentException("Only EntityReferenceType or ValueObjectType is a legal argument");
    }
}
