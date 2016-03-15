package org.javers.guava;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumeratorContext;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.CollectionType;

import java.lang.reflect.Type;

/**
 * @author akrystian
 */
public class MultisetType extends CollectionType{

    public MultisetType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceMultiset_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);
        Multiset sourceMultiset = toNotNullMultiset(sourceMultiset_);
        Multiset targetMultiset = HashMultiset.create();

        SetEnumeratorContext enumeratorContext = new SetEnumeratorContext();
        owner.setEnumeratorContext(enumeratorContext);

        for (Object sourceVal : sourceMultiset) {
            targetMultiset.add(mapFunction.apply(sourceVal, owner));
            enumeratorContext.nextId();
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

    private class SetEnumeratorContext implements EnumeratorContext{
        int randomId = 0;
        @Override
        public String getPath() {
            return "random_"+randomId;
        }

        void nextId(){
            randomId++;
        }
    }
}