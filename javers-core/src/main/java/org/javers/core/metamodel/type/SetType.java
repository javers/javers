package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetType extends CollectionType{

    public SetType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceSet_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);
        Set sourceSet = toNotNullSet(sourceSet_);
        Set targetSet = new HashSet(sourceSet.size());

        EnumerationAwareOwnerContext enumerationContext = new SetEnumerationOwnerContext(owner);
        for (Object sourceVal : sourceSet) {
            targetSet.add(mapFunction.apply(sourceVal, enumerationContext));
        }
        return Collections.unmodifiableSet(targetSet);
    }

    private Set toNotNullSet(Object sourceSet) {
        if (sourceSet == null) {
            return Collections.emptySet();
        }
        else{
            return (Set)sourceSet;
        }
    }

    /**
     * marker class
     */
    public static class SetEnumerationOwnerContext extends EnumerationAwareOwnerContext {
        SetEnumerationOwnerContext(OwnerContext ownerContext) {
            super(ownerContext);
        }
    }
}
