package org.javers.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.KeyValueType;
import org.javers.core.metamodel.type.MapEnumerationOwnerContext;
import org.javers.core.metamodel.type.MapType;

import java.lang.reflect.Type;
import java.util.function.Function;

import static org.javers.guava.Multimaps.toNotNullMultimap;


/**
 * @author akrystian
 */
public class MultimapType extends KeyValueType {

    public static MultimapType getInstance(){
        return new MultimapType(Multimap.class);
    }

    public MultimapType(Type baseJavaType) {
        super(baseJavaType,2);
    }

    /**
     * @return immutable Multimap
     */
    @Override
    public Multimap map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);

        Multimap sourceMultimap = toNotNullMultimap(sourceEnumerable);
        Multimap targetMultimap = ArrayListMultimap.create();
        MapEnumerationOwnerContext enumeratorContext = new MapEnumerationOwnerContext(owner, true);

        MapType.mapEntrySet(sourceMultimap.entries(), mapFunction, enumeratorContext, (k,v) -> targetMultimap.put(k,v));

        return Multimaps.unmodifiableMultimap(targetMultimap);
    }

    @Override
    public boolean isEmpty(Object container){
        return container == null || ((Multimap) container).isEmpty();
    }

    /**
     * Nulls keys are filtered
     */
    @Override
    public Object map(Object sourceEnumerable, Function mapFunction) {
        Validate.argumentIsNotNull(mapFunction);

        Multimap sourceMultimap = toNotNullMultimap(sourceEnumerable);
        Multimap targetMultimap = ArrayListMultimap.create();

        MapType.mapEntrySetFilterNulls(sourceMultimap.entries(), mapFunction, (k,v) -> targetMultimap.put(k,v));

        return targetMultimap;
    }
}
