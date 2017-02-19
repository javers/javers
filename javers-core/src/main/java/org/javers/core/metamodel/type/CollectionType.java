package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.javers.common.collections.Collections.wrapNull;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {

    public CollectionType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public boolean isEmpty(Object collection) {
        return collection == null || ((Collection)collection).isEmpty();
    }

    /**
     * @return immutable Set
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);
        Collection sourceCol = wrapNull(sourceEnumerable);
        Set targetSet = new HashSet(sourceCol.size());

        EnumerationAwareOwnerContext enumerationContext = new EnumerationAwareOwnerContext(owner, true);
        for (Object sourceVal : sourceCol) {
            targetSet.add(mapFunction.apply(sourceVal, enumerationContext));
        }
        return Collections.unmodifiableSet(targetSet);
    }

    /**
     * Nulls are filtered
     */
    @Override
    public Object map(Object sourceEnumerable, Function mapFunction) {
        Collection sourceCol = wrapNull(sourceEnumerable);
        return sourceCol.stream().map(mapFunction).filter(it -> it != null).collect(Collectors.toSet());
    }
}
