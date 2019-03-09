package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
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
     * @return immutable List
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return mapToList(sourceEnumerable, mapFunction, owner);
    }

    /**
     * @return immutable List
     */
    protected Object mapToList(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);
        Collection sourceCol = wrapNull(sourceEnumerable);

        EnumerationAwareOwnerContext enumerationContext = new EnumerationAwareOwnerContext(owner, true);
        List targetList = new ArrayList();
        for (Object sourceVal : sourceCol) {
            targetList.add(mapFunction.apply(sourceVal, enumerationContext));
        }
        return Collections.unmodifiableList(targetList);
    }

    /**
     * @return immutable Set
     */
    protected Object mapToSet(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);
        Collection sourceCol = wrapNull(sourceEnumerable);
        Set targetSet = new HashSet(sourceCol.size());

        EnumerationAwareOwnerContext enumerationContext = new EnumerationAwareOwnerContext(owner, true);
        for (Object sourceVal : sourceCol) {
            targetSet.add(mapFunction.apply(sourceVal, enumerationContext));
        }
        return unmodifiableSet(targetSet);
    }


    /**
     * Nulls are filtered
     */
    @Override
    public Object map(Object sourceEnumerable, Function mapFunction) {
        Collection sourceCol = wrapNull(sourceEnumerable);
        return unmodifiableSet(
                (Set) sourceCol.stream().map(mapFunction).filter(it -> it != null).collect(toSet()) );
    }

    @Override
    public Object empty() {
        return Collections.emptyList();
    }

    @Override
    protected Stream<Object> items(Object source) {
        return wrapNull(source).stream();
    }
}
