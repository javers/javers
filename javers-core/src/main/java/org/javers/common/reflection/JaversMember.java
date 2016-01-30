package org.javers.common.reflection;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;

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

    /**
     * @param resolvedReturnType nullable
     */
    public JaversMember(T rawMember, Type resolvedReturnType) {
        Validate.argumentIsNotNull(rawMember);
        this.rawMember = rawMember;
        this.resolvedReturnType = Optional.fromNullable(resolvedReturnType);
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

    public boolean hasAnyAnnotation(Set<String> annotationNames){
        for (String annotationName : annotationNames){
            if (isAnnotationPresent(annotationName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnnotationPresent(String annotationName){
        Validate.argumentsAreNotNull(annotationName);

        if (contains(( (AccessibleObject)rawMember).getAnnotations(), annotationName)) {
            return true;
        }

        return false;
    }

    public abstract Object invokeEvenIfPrivate(Object target);

    protected void setAccessibleIfNecessary() {
        if(!isPublic(rawMember))
        {
            ((AccessibleObject)rawMember).setAccessible(true); //that's Java Reflection API ...
        }
    }

    private boolean isPublic(Member member){
        return Modifier.isPublic(member.getModifiers());
    }

    private static boolean contains(Annotation[] annotations, String annotationName) {
        for (Annotation a : annotations){
            if (a.annotationType().getSimpleName().equals(annotationName)){
                return true;
            }
        }
        return false;
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
