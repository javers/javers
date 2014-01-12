package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * EntityManager bootstrap is two-phased:
 * <ol>
 *     <li/>JaVers bootstrap should
 *          registering client's Entities and ValueObjects through {@link #register(ManagedClassDefinition)}.
 *          In this phase, EntityManager creates proper {@link JaversType}'s in {@link TypeMapper}.
 *     <li/>When all types are registered, JaVers bootstrap calls {@link #buildManagedClasses()},
 *          in order to create Entities and ValueObjects for all previously registered types.
 * </ol>
 *
 * @author bartosz walacik
 */
public class EntityManager {
    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

    private final EntityFactory entityFactory;
    private final TypeMapper typeMapper;

    private final Set<ManagedClassDefinition> managedClassDefinitions = new HashSet<>();
    private final ManagedClasses managedClasses = new ManagedClasses();

    public EntityManager(EntityFactory entityFactory, TypeMapper typeMapper) {
        argumentsAreNotNull(entityFactory, typeMapper);

        this.entityFactory = entityFactory;
        this.typeMapper = typeMapper;
    }

    /**
     * @throws JaversException if class is not managed or EntityManager is not initialized
     */
    public ManagedClass getByClass(Class<?> clazz) {
        if (!isRegistered(clazz)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, clazz.getName());
        }
        return managedClasses.getBySourceClass(clazz);
    }

    public void register(ManagedClassDefinition def) {
        Validate.argumentIsNotNull(def);

        if (isRegistered(def)) {
            return; //already managed
        }

        //TODO REFACTOR
        if (def instanceof EntityDefinition) {
            typeMapper.registerEntityReferenceType(def.getClazz());
        }
        if (def instanceof  ValueObjectDefinition) {
            typeMapper.registerValueType(def.getClazz());
        }
        managedClassDefinitions.add(def);
    }

    public void registerEntity(Class<?> clazz) {
        register(new EntityDefinition(clazz));
    }

    public void registerValueObject(Class<?> clazz) {
        register(new ValueObjectDefinition(clazz));
    }

    private boolean isRegistered(ManagedClassDefinition def) {
        return managedClassDefinitions.contains(def);
    }

    private boolean isRegistered(Class clazz) {
        //TODO optimize this lame loop
        for (ManagedClassDefinition def : managedClassDefinitions) {
            if (def.getClazz() == clazz){
                return true;
            }
        }
        return false;
    }

    public boolean isManaged(Class<?> clazz) {
        return managedClasses.containsManagedClassWithSourceClass(clazz);
    }

    /**
     * call that if all Entities and ValueObject are registered
     */
    public void buildManagedClasses() {
        for (ManagedClassDefinition def : managedClassDefinitions) {
             if (def instanceof  EntityDefinition) {
                manageEntity((EntityDefinition)def);
            }
            if (def instanceof  ValueObjectDefinition) {
                manageValueObject((ValueObjectDefinition)def);
            }
        }
    }

    private void manageEntity(EntityDefinition entityDef) {
        logger.debug("registering Entity[{}]", entityDef.getClazz().getName());
        managedClasses.add(entityFactory.create(entityDef));
    }

    private void manageValueObject(ValueObjectDefinition voDef) {
        logger.debug("registering ValueObject[{}]", voDef.getClazz().getName());
        managedClasses.add(entityFactory.create(voDef));
    }
}
