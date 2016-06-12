package org.javers.guava.multiset;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.CollectionType;

/**
 * @author akrystian
 */
public class MultisetType extends CollectionType{

    public MultisetType() {
        super(Multiset.class);
    }

    @Override
    public Object map(Object sourceMultiset_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);
        Multiset sourceMultiset = toNotNullMultiset(sourceMultiset_);
        Multiset targetMultiset = HashMultiset.create();

        EnumerationAwareOwnerContext enumeratorContext = new MultisetEnumerationOwnerContext(owner);
        for (Object sourceVal : sourceMultiset) {
            targetMultiset.add(mapFunction.apply(sourceVal, enumeratorContext));
        }
        return Multisets.unmodifiableMultiset(targetMultiset);
    }

    private Multiset toNotNullMultiset(Object sourceSet) {
        if (sourceSet == null) {
            return HashMultiset.create();
        }
        else{
            return (HashMultiset)sourceSet;
        }
    }

    /**
     * marker class
     */
    public static class MultisetEnumerationOwnerContext extends EnumerationAwareOwnerContext{
        MultisetEnumerationOwnerContext(OwnerContext ownerContext) {
            super(ownerContext);
        }
    }
}