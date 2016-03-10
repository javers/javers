package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.EnumeratorContext;
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

        SetEnumeratorContext enumerator = new SetEnumeratorContext();
        EnumerationAwareOwnerContext enumerationOwnerContext =
                new EnumerationAwareOwnerContext(enumerator, owner);

        for (Object sourceVal : sourceSet) {
            targetSet.add(mapFunction.apply(sourceVal, enumerationOwnerContext));
            enumerator.next(sourceVal);
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

    private class SetEnumeratorContext implements EnumeratorContext {
        int randomId = 0;

        @Override
        public String getPath() {
            return "random_"+(randomId++);
        }

        public void next(Object sourceVal) {
        }
    }
}
