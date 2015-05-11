package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.clazz.ManagedClassFactory;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

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
    public String toString() {
        StringBuilder b = new StringBuilder(super.toString()+"\n");
        b.append("properties {"+"\n");
        for (Property p : managedClass.getProperties()) {
            b.append("  " + p.toString() + "\n");
        }
        b.append("}");
        return b.toString();
    }
}
