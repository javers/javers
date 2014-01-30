package org.javers.core.metamodel.property;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * EntityManager bootstrap is two-phased:
 * <ol>
 *     <li/>JaVers bootstrap should registering client's Entities and ValueObjects.
 *     <li/>When all types are registered, JaVers bootstrap calls {@link #buildManagedClasses()},
 *          in order to create Entities and ValueObjects for all previously registered types.
 * </ol>
 *
 * @author bartosz walacik
 */
public class EntityManager {
    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

    private final ManagedClassFactory managedClassFactory;
    private final Set<ManagedClassDefinition> managedClassDefinitions = new HashSet<>();
    private final Map<Class,ManagedClass> managedClasses = new HashMap<>();

    public EntityManager(ManagedClassFactory managedClassFactory) {
        argumentsAreNotNull(managedClassFactory);
        this.managedClassFactory = managedClassFactory;
    }

    /**
     * @throws JaversException if class is not managed or EntityManager is not initialized
     */
    public ManagedClass getByClass(Class<?> clazz) {
        if (!isRegistered(clazz)) {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, clazz.getName());
        }
        return managedClasses.get(clazz);
    }

    public void register(ManagedClassDefinition def) {
        Validate.argumentIsNotNull(def);

        if (isRegistered(def)) {
            return; //already managed
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
        return managedClasses.containsKey(clazz);
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
        managedClasses.put(entityDef.getClazz(), managedClassFactory.create(entityDef));
    }

    private void manageValueObject(ValueObjectDefinition voDef) {
        logger.debug("registering ValueObject[{}]", voDef.getClazz().getName());
        managedClasses.put(voDef.getClazz(), managedClassFactory.create(voDef));
    }
}
