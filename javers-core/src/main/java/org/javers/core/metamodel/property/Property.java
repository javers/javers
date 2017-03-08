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
    private transient final boolean hasShallowReferenceAnn;

    public Property(JaversMember member, boolean hasTransientAnn, boolean hasShallowReferenceAnn){
        argumentIsNotNull(member);
        this.member = member;
        this.hasTransientAnn = hasTransientAnn;
        this.hasShallowReferenceAnn = hasShallowReferenceAnn;
    }

    public Property(JaversMember member, boolean hasTransientAnn){
        this(member, hasTransientAnn, false);
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
            return  member.getEvenIfPrivate(target);
        } catch (JaversException e) {
            if (e.getCode() == JaversExceptionCode.MISSING_PROPERTY) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Sets property value, even if private.
     * <br/>
     * Swallows JaversException.MISSING_PROPERTY
     *
     * @param target invocation target
     * @param value value to be set
     */
    public void set(Object target, Object value) {
        try {
            member.setEvenIfPrivate(target, value);
        } catch (JaversException e) {
            if (e.getCode() == JaversExceptionCode.MISSING_PROPERTY) {
                return; //swallowed
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

    public boolean hasShallowReferenceAnn() {
        return hasShallowReferenceAnn;
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
