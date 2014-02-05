package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.ManagedClass;

/**
 * @author bartosz walacik
 */
public class ManagedType extends JaversType {
    private final ManagedClass managedClass;

    protected ManagedType(ManagedClass managedClass) {
        super(managedClass.getSourceClass());
        Validate.argumentIsNotNull(managedClass);
        this.managedClass = managedClass;
    }

    ManagedClass getManagedClass() {
        return managedClass;
    }
}
