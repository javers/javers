package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.*;

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

    @Override
    public Object map(Object sourceCol_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);
        Collection<?> sourceCol = (Collection) sourceCol_;
        if (sourceCol == null || sourceCol.isEmpty()) {
            return Collections.emptySet();
        }

        Set targetSet = new HashSet(sourceCol.size());
        EnumerationAwareOwnerContext enumerationContext = new CollectionEnumerationOwnerContext(owner);
        for (Object sourceVal : sourceCol) {
            targetSet.add(mapFunction.apply(sourceVal, enumerationContext));
        }
        return Collections.unmodifiableSet(targetSet);
    }

    /**
     * marker class
     */
    public static class CollectionEnumerationOwnerContext extends EnumerationAwareOwnerContext {
        CollectionEnumerationOwnerContext(OwnerContext ownerContext) {
            super(ownerContext);
        }
    }
}
