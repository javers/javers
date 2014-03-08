package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.ManagedClassFactory;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
abstract class ManagedType extends JaversType {
    private final ManagedClass managedClass;

    ManagedType(ManagedClass managedClass) {
        super(managedClass.getSourceClass());
        Validate.argumentIsNotNull(managedClass);
        this.managedClass = managedClass;
    }

    ManagedClass getManagedClass() {
        return managedClass;
    }

    abstract ManagedType spawn(Class javaType, ManagedClassFactory managedClassFactory);
}
