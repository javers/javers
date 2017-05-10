package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.clazz.EntityDefinition;

/**
 * @author bartosz.walacik
 */
class EntityTypeFactory {
    private final ManagedClassFactory managedClassFactory;

    EntityTypeFactory(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;
    }

    EntityType createEntity(EntityDefinition definition) {
        ManagedClass managedClass = managedClassFactory.create(definition);

        JaversProperty idProperty;
        if (definition.hasCustomId()) {
            idProperty = managedClass.getProperty(definition.getIdPropertyName());
        } else {
            idProperty = findDefaultIdProperty(managedClass);
        }

        if (definition.isShallowReference()) {
            return new ShallowReferenceType(managedClass, idProperty, definition.getTypeName());
        } else {
            return new EntityType(managedClass, idProperty, definition.getTypeName());
        }
    }

    /**
     * @throws JaversException ENTITY_WITHOUT_ID
     */
    private JaversProperty findDefaultIdProperty(ManagedClass managedClass) {
        if (managedClass.getLooksLikeId().isEmpty()) {
            throw new JaversException(JaversExceptionCode.ENTITY_WITHOUT_ID, managedClass.getBaseJavaClass().getName());
        }
        return managedClass.getLooksLikeId().get(0);
    }
}
