package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static org.javers.common.collections.Collections.wrapNull;

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
    public Object map(Object sourceOptional_, Function mapFunction) {
        Validate.argumentsAreNotNull(sourceOptional_, mapFunction);
        Optional sourceOptional = (Optional)sourceOptional_;
        return sourceOptional.map(o -> mapFunction.apply(o));
    }

    @Override
    protected Stream<Object> items(Object source) {
        if (source == null) {
            return Stream.empty();
        }
        Optional sourceOptional = (Optional)source;
        return (Stream)sourceOptional.map(it -> Stream.of(it)).orElse(Stream.empty());
    }

    @Override
    public boolean isEmpty(Object optional){
        return optional == null || !((Optional)optional).isPresent();
    }

    @Override
    public Object empty() {
        return Optional.empty();
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return Optional.class;
    }
}
