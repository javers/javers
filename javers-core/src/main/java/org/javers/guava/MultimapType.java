package org.javers.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Maps;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.javers.guava.Multimaps.toNotNullMultimap;


/**
 * @author akrystian
 */
public class MultimapType extends KeyValueType {

    public MultimapType(Type baseJavaType, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, 2, typeMapperLazy);
    }

    /**
     * @return immutable Multimap
     */
    @Override
    public Multimap map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);

        Multimap sourceMultimap = toNotNullMultimap(sourceEnumerable);
        Multimap targetMultimap = ArrayListMultimap.create();
        MapEnumerationOwnerContext enumeratorContext = new MapEnumerationOwnerContext(this, owner, true);

        MapType.mapEntrySet(this, sourceMultimap.entries(), mapFunction, enumeratorContext, (k,v) -> targetMultimap.put(k,v), false);

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
    public Object map(Object sourceEnumerable, Function mapFunction, boolean filterNulls) {
        Validate.argumentIsNotNull(mapFunction);

        Multimap sourceMultimap = toNotNullMultimap(sourceEnumerable);
        Multimap targetMultimap = ArrayListMultimap.create();

        MapType.mapEntrySet(this, sourceMultimap.entries(), mapFunction, (k,v) -> targetMultimap.put(k,v), filterNulls);

        return targetMultimap;
    }

    @Override
    protected Stream<Map.Entry> entries(Object source) {
        Map sourceMap = Maps.wrapNull(source);
        return sourceMap.entrySet().stream();
    }

    @Override
    public Object empty() {
        return ArrayListMultimap.create();
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return Multimap.class;
    }
}
