package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.core.metamodel.scanner.ClassScan;

/**
 * @author bartosz.walacik
 */
class EntityTypeFactory {
    private final ManagedClassFactory managedClassFactory;

    EntityTypeFactory(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;
    }

    EntityType createEntity(EntityDefinition definition, ClassScan scan) {
        ManagedClass managedClass = managedClassFactory.create(definition, scan);

        JaversProperty idProperty;
        if (definition.hasCustomId()) {
            idProperty = managedClass.getProperty(definition.getIdPropertyName());
        } else {
            idProperty = findDefaultIdProperty(managedClass, definition.isShallowReference());
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
    private JaversProperty findDefaultIdProperty(ManagedClass managedClass, boolean isShallowReference) {
        if (managedClass.getLooksLikeId().isEmpty()) {
            JaversExceptionCode code = isShallowReference ?
                    JaversExceptionCode.SHALLOW_REF_ENTITY_WITHOUT_ID :
                    JaversExceptionCode.ENTITY_WITHOUT_ID;
            throw new JaversException(code, managedClass.getBaseJavaClass().getName());
        }
        return managedClass.getLooksLikeId().get(0);
    }
}
