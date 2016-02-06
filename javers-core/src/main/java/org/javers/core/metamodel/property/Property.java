package org.javers.core.metamodel.property;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.JaversMember;

import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Domain object's data property, getter or field
 */
//TODO move to another package
public class Property {
    public static final String ID_ANN = "Id";
    public static final String EMBEDDED_ID_ANN = "EmbeddedId";

    private transient final JaversMember member;
    private transient final boolean hasTransientAnn;

    public Property(JaversMember member, boolean hasTransientAnn){
        argumentIsNotNull(member);
        this.member = member;
        this.hasTransientAnn = hasTransientAnn;
    }

    public Type getGenericType() {
        return member.getGenericResolvedType();
    }

    /**
     * use getGenericType() when possible, see JaversMember.resolvedReturnType
     */
    public Class<?> getRawType() {
        return member.getRawType();
    }

    /**
     * true if property looks like identifier of an Entity, for example has @Id annotation
     */
    public boolean looksLikeId() {
        return member.isAnnotationPresent(ID_ANN) || member.isAnnotationPresent(EMBEDDED_ID_ANN);
    }

    /**
     * Returns property value, even if private.
     * <br/>
     * Converts JaversException.MISSING_PROPERTY to null.
     *
     * @param target invocation target
     */
    public Object get(Object target) {
        try {
            return  member.invokeEvenIfPrivate(target);
        } catch (JaversException e) {
            if (e.getCode() == JaversExceptionCode.MISSING_PROPERTY) {
                return null;
            }
            throw e;
        }
    }

    public boolean isNull(Object target) {
        return get(target) == null;
    }

    public String getName() {
        return member.propertyName();
    }

    public boolean hasTransientAnn() {
        return hasTransientAnn;
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

    public JaversMember getMember() {
        return member;
    }
}
