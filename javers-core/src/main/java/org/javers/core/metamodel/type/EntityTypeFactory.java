package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.core.metamodel.scanner.ClassScan;

import java.util.List;
import java.util.stream.Collectors;

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

        List<JaversProperty> idProperties;
        if (definition.hasExplicitId()) {
            idProperties = managedClass.getProperties(definition.getIdPropertyNames());
        } else {
            idProperties = findDefaultIdProperties(managedClass, definition.isShallowReference());
        }

        if (definition.isShallowReference()) {
            return new ShallowReferenceType(managedClass, idProperties, definition.getTypeName());
        } else {
            return new EntityType(managedClass, idProperties, definition.getTypeName());
        }
    }

    /**
     * @throws JaversException ENTITY_WITHOUT_ID
     */
    private List<JaversProperty> findDefaultIdProperties(ManagedClass managedClass, boolean isShallowReference) {
        if (managedClass.getLooksLikeId().isEmpty()) {
            JaversExceptionCode code = isShallowReference ?
                    JaversExceptionCode.SHALLOW_REF_ENTITY_WITHOUT_ID :
                    JaversExceptionCode.ENTITY_WITHOUT_ID;
            throw new JaversException(code, managedClass.getBaseJavaClass().getName());
        }
        return managedClass.getLooksLikeId();
    }
}
