package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Function;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;

/**
 * @author bartosz.walacik
 */
public class OptionalType extends CollectionType {

    public OptionalType(Type baseJavaType) {
        super(baseJavaType);
    }

    public OptionalType() {
        super(java.util.Optional.class);
    }

    @Override
    public Object map(Object sourceOptional_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(sourceOptional_, mapFunction);

        java.util.Optional sourceOptional = toNormalizedOptional(sourceOptional_);

        if (!sourceOptional.isPresent()){
            return java.util.Optional.empty();
        }

        Object applied = mapFunction.apply(sourceOptional.get(), new EnumerationAwareOwnerContext(owner));

        return java.util.Optional.of( applied );
    }

    @Override
    public boolean isEmpty(Object optional){
        return optional == null || !toNormalizedOptional(optional).isPresent();
    }

    private Object get(Object sourceOptional_){
        java.util.Optional sourceOptional = toNormalizedOptional(sourceOptional_);

        if (sourceOptional.isPresent()) {
            return sourceOptional.get();
        }
        return null;
    }

    public Object mapAndGet(Object sourceOptional, Function mapFunction){
        return mapFunction.apply(get(sourceOptional));
    }

    /**
     * converts nulls to Optional.empty()
     */
    public Object normalize(Object sourceOptional){
        if (sourceOptional == null){
            return java.util.Optional.empty();
        }
        return sourceOptional;
    }

    /**
     * we need this hack be compatible with java7
     */
    private java.util.Optional toOptional(Object optional){
        if (! (optional instanceof java.util.Optional)) {
            throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "instance of Optional expected");
        }

        return (java.util.Optional) optional;
    }

    private java.util.Optional toNormalizedOptional(Object optional){
        return (java.util.Optional)normalize( toOptional(optional) );
    }
}
