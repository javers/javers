package org.javers.core.metamodel.type;

import org.javers.common.string.PrettyPrintBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.clazz.ManagedClassFactory;
import org.javers.core.metamodel.object.GlobalId;

import java.lang.reflect.Type;

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

    @Override
    protected Type getRawDehydratedType() {
        return GlobalId.class;
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addMultiField("managedProperties", managedClass.getProperties());
    }
}
