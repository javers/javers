package org.javers.guava;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.SetType;

import java.lang.reflect.Type;

/**
 * @author akrystian
 */
public class MultisetType extends CollectionType{

    public static MultisetType getInstance(){
        return  new MultisetType(Multiset.class);
    }

    public MultisetType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);
        Multiset sourceMultiset = toNotNullMultiset(sourceEnumerable);
        Multiset targetMultiset = HashMultiset.create();

        EnumerationAwareOwnerContext enumeratorContext = new EnumerationAwareOwnerContext(owner, true);
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
            return (Multiset)sourceSet;
        }
    }
}