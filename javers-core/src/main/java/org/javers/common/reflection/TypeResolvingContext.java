package org.javers.common.reflection;

import org.javers.common.validation.Validate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
class TypeResolvingContext {
    private Map<TypeVariable, Type> substitutions = new HashMap<>();

    Type getSubstitution(Type type) {
        if (type instanceof  TypeVariable){
            Type resolved =  substitutions.get(type);
            if (resolved instanceof TypeVariable) {
                return getSubstitution(resolved);
            }
            else {
                return resolved;
            }
        }
        return null;
    }

    void addTypeSubstitutions(Class clazz){
        Validate.argumentIsNotNull(clazz);

        if (clazz == Object.class){
            return;
        }

        Type t = clazz.getGenericSuperclass();
        if ( !(t instanceof ParameterizedType)){
            return;
        }

        ParameterizedType genericSuperclass = (ParameterizedType)t;
        Class superclass = clazz.getSuperclass();

        TypeVariable[] typeParameters = superclass.getTypeParameters();
        Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();

        if (typeParameters.length == 0 || actualTypeArguments.length == 0){
            return;
        }

        //both arrays should have the same length, hopefully
        for (int i=0; i<typeParameters.length; i++){
            TypeVariable typeParam = typeParameters[i];
            Type typeArg = actualTypeArguments[i];
            substitutions.put(typeParam, typeArg);
        }
    }
}
