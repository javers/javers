package org.javers.common.reflection;

import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Enhanced Field or Method, deals with Java type erasure.
 * <br/><br/>
 *
 * A Member (getter or field) inherited from a Generic superclass with actual (return) type resolved.
 * <br/><br/>
 *
 * Formal type parameter of superclass is resolved to actual type argument of subclass.
 *
 * @author bartosz walacik
 */
public abstract class JaversMember<T extends Member> {
    private final T rawMember; //delegate
    private final Optional<Type> resolvedReturnType;
    private final boolean looksLikeId;

    /**
     * @param resolvedReturnType nullable
     */
    public JaversMember(T rawMember, Type resolvedReturnType) {
        this(rawMember, resolvedReturnType, false);
    }

    public JaversMember(final T rawMember, final Type resolvedReturnType, final boolean looksLikeId) {
        Validate.argumentIsNotNull(rawMember);
        this.rawMember = rawMember;
        this.resolvedReturnType = Optional.ofNullable(resolvedReturnType);
        this.looksLikeId = looksLikeId || hasAnnotation(Sets.asSet(Property.ID_ANN, Property.EMBEDDED_ID_ANN));
        setAccessibleIfNecessary(rawMember);
    }

    protected abstract Type getRawGenericType();

    public abstract Class<?> getRawType();

    public T getRawMember() {
        return rawMember;
    }

    public Type getGenericResolvedType(){
        if (resolvedReturnType.isPresent()){
            return resolvedReturnType.get();
        }
        return getRawGenericType();
    }

    public Class<?> getDeclaringClass(){
        return rawMember.getDeclaringClass();
    }

    public String name(){
        return rawMember.getName();
    }

    public String propertyName(){
        return rawMember.getName();
    }

    public Set<Annotation> getAnnotations() {
        return unmodifiableSet(Sets.asSet(((AccessibleObject) rawMember).getAnnotations()));
    }

    public Set<Class<? extends Annotation>> getAnnotationTypes() {
        return Sets.transform(getAnnotations(), ann -> ann.annotationType());
    }

    public boolean hasAnnotation(Set<String> aliases) {
        if (aliases == null) {
            return false;
        }
        return getAnnotationTypes().stream().anyMatch(annType -> aliases.contains(annType.getSimpleName()));
    }

    public boolean looksLikeId() {
        return looksLikeId;
    }

    public abstract Object getEvenIfPrivate(Object target);

    public abstract void setEvenIfPrivate(Object target, Object value);

    void setAccessibleIfNecessary(Member rawMember) {
        if(!isPublic(rawMember))
        {
            ((AccessibleObject)rawMember).setAccessible(true); //that's Java Reflection API ...
        }
    }

    private boolean isPublic(Member member){
        return Modifier.isPublic(member.getModifiers());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JaversMember that = (JaversMember) o;

        return this.rawMember.equals(that.rawMember) && this.resolvedReturnType.equals(that.resolvedReturnType);
    }

    @Override
    public int hashCode() {
        return rawMember.hashCode() + resolvedReturnType.hashCode();
    }
}
