package org.javers.core.metamodel.type;

import org.javers.common.reflection.ReflectionUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * ValueObject class in client's domain model.
 * <br/><br/>
 *
 * Has list of mutable properties but no unique identifier.
 * <br/><br/>
 *
 * Two ValueObjects are compared property by property.
 * <br/><br/>
 *
 * Example:
 * <pre>
 *     class Address {
 *         private String city;
 *         private String street;
 *         private String zip;
 *         ...
 *     }
 * </pre>
 *
 * @author bartosz walacik
 */
public class ValueObjectType extends ManagedType{
    private final boolean defaultType;
    private final Optional<Function<Object, String>> toStringFunction = Optional.empty();

    ValueObjectType(ManagedClass valueObject){
        super(valueObject);
        this.defaultType = false;
    }

    public ValueObjectType(Class baseJavaClass, List<JaversProperty> allProperties){
        this(new ManagedClass(baseJavaClass, allProperties, Collections.emptyList(), ManagedPropertiesFilter.empty()));
    }

    ValueObjectType(ManagedClass valueObject, Optional<String> typeName, boolean isDefault) {
        super(valueObject, typeName);
        this.defaultType = isDefault;
    }

    @Override
    ValueObjectType spawn(ManagedClass managedClass, Optional<String> typeName) {
        return new ValueObjectType(managedClass, typeName, defaultType);
    }

    @Override
    public boolean canBePrototype() {
        return !defaultType;
    }

    public String smartToString(Object value) {
        if (value == null) {
            return "";
        }

        return toStringFunction
                .map(f -> f.apply(value))
                .orElse(ReflectionUtil.reflectiveToString(value));
    }
}
