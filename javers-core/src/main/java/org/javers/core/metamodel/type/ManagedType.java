package org.javers.core.metamodel.type;

import java.util.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.string.PrettyPrintBuilder;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author bartosz walacik
 */
public abstract class ManagedType extends JaversType {
    private final ManagedClass managedClass;

    ManagedType(ManagedClass managedClass) {
        this(managedClass, Optional.<String>empty());
    }

    ManagedType(ManagedClass managedClass, Optional<String> typeName) {
        super(managedClass.getBaseJavaClass(), typeName);
        this.managedClass = managedClass;
    }

    abstract ManagedType spawn(ManagedClass managedClass, Optional<String> typeName);

    @Override
    protected Type getRawDehydratedType() {
        return GlobalId.class;
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addMultiField("managedProperties", managedClass.getManagedProperties());
    }

    /**
     * @throws JaversException PROPERTY_NOT_FOUND
     */
    public Property getProperty(String propertyName) {
        return managedClass.getProperty(propertyName);
    }

    public List<Property> getProperties(Predicate<Property> query) {
        return managedClass.getManagedProperties(query);
    }

    public List<Property> getProperties() {
        return managedClass.getManagedProperties();
    }

    public Set<String> getPropertyNames(){
        return managedClass.getPropertyNames();
    }

    ManagedClass getManagedClass() {
        return managedClass;
    }
}
