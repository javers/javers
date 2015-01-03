package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.clazz.ManagedClassFactory;

/**
 * @author bartosz walacik
 */
public abstract class ManagedType extends JaversType {
    private final ManagedClass managedClass;

    ManagedType(ManagedClass managedClass) {
        super(managedClass.getClientsClass());
        Validate.argumentIsNotNull(managedClass);
        this.managedClass = managedClass;
    }

    public ManagedClass getManagedClass() {
        return managedClass;
    }

    abstract ManagedType spawn(Class javaType, ManagedClassFactory managedClassFactory);
}
