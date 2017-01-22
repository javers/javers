package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author bartosz.walacik
 */
public class OptionalType extends CollectionType {

    /** for TypeFactory.spawnFromPrototype() */
    public OptionalType(Type baseJavaType) {
        super(baseJavaType);
    }

    public OptionalType() {
        super(java.util.Optional.class);
    }

    @Override
    public Object map(Object sourceOptional_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(sourceOptional_, mapFunction);
        Optional sourceOptional = (Optional)sourceOptional_;
        return sourceOptional.map(o -> mapFunction.apply(o, new EnumerationAwareOwnerContext(owner)));
    }

    @Override
    public boolean isEmpty(Object optional){
        return optional == null || !((Optional)optional).isPresent();
    }
}
