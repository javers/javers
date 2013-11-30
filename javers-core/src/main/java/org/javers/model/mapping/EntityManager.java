package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.EntityReferenceType;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.mapping.type.ValueObjectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

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

    private final Set<EntityDefinition> entityDefinitions = new HashSet<>();
    private final ManagedClasses managedClasses = new ManagedClasses();

    public EntityManager(EntityFactory entityFactory, ValueObjectFactory valueObjectFactory, TypeMapper typeMapper) {
        argumentsAreNotNull(entityFactory, valueObjectFactory, typeMapper);

        this.entityFactory = entityFactory;
        this.typeMapper = typeMapper;
        this.valueObjectFactory = valueObjectFactory;
    }

    /**
     * @throws JaversException if class is not managed or EntityManager is not initialized
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

    @Deprecated
    public void registerEntity(Class<?> entityClass) {
        registerEntity(new EntityDefinition(entityClass));
    }

    public void registerEntity(EntityDefinition def) {
        Validate.argumentIsNotNull(def);

        if (isRegisterd(def)) {
            return; //already managed
        }

        typeMapper.registerEntityReferenceType(def.getClazz());
        entityDefinitions.add(def);
    }

    //TODO refactor to ValueObjectDefinition extends ManagedClassDefinition
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

    private boolean isRegisterd(EntityDefinition def) {
        return entityDefinitions.contains(def);
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
        for (EntityDefinition entityDef : entityDefinitions) {
            manageEntity(entityDef);
        }
        for (ValueObjectType voType : typeMapper.getMappedValueObjectTypes()) {
            manageValueObject(voType.getBaseJavaType());
        }
    }

    private void manageEntity(EntityDefinition entityDef) {
        managedClasses.add(entityFactory.create(entityDef));
    }

    private void manageValueObject(Class classToManage) {
        managedClasses.add(valueObjectFactory.create(classToManage));
    }
}
