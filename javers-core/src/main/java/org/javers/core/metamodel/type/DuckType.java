package org.javers.core.metamodel.type;

import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author bartosz.walacik
 *
 * @since 1.4
 */
public class DuckType {
    private final String typeName;
    private final Set<String> properties;

    public DuckType(ManagedType managedType) {
        this(managedType.getName(), managedType.getPropertyNames());
    }

    public DuckType(String typeName) {
        this(typeName, (Set)Collections.emptySet());
    }

    public DuckType(String typeName, Set<String> properties) {
        Validate.argumentsAreNotNull(typeName, properties);
        this.typeName = typeName;
        this.properties = new HashSet<>(properties);
    }

    public DuckType bareCopy(){
        return new DuckType(typeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuckType duckType = (DuckType) o;
        return Objects.equals(typeName, duckType.typeName) &&
                Objects.equals(properties, duckType.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, properties);
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return "DuckType{" +
                "typeName='" + typeName + '\'' +
                ", properties=" + ToStringBuilder.setToString(properties) +
                '}';
    }

    public boolean isBare(){
        return properties.isEmpty();
    }
}
