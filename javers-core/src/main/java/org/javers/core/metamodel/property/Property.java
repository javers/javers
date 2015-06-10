package org.javers.core.metamodel.property;

import org.javers.common.reflection.JaversMember;

import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Domain object's data property, getter or field
 */
public class Property {
    public static final String ID_ANN = "Id";

    private transient final JaversMember member;

    Property(JaversMember member) {
        argumentIsNotNull(member);
        this.member = member;
    }

    public Type getGenericType() {
        return member.getGenericType();
    }

    public Class<?> getType() {
        return member.getType();
    }

    /**
     * true if property looks like identifier of an Entity, for example has @Id annotation
     */
    public boolean looksLikeId() {
        return member.isAnnotationPresent(ID_ANN);
    }

    /**
     * returns property value, even if private
     *
     * @param target invocation target
     */
    public Object get(Object target) {
        return member.invokeEvenIfPrivate(target);
    }

    public boolean isNull(Object target) {
        return get(target) == null;
    }

    public String getName() {
        return member.propertyName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property that = (Property) o;
        return member.equals(that.member);
    }

    @Override
    public int hashCode() {
        return member.hashCode();
    }

    @Override
    public String toString() {
        return member.toString();
    }
}
