package org.javers.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.KeyValueType;
import org.javers.core.metamodel.type.MapEnumerationOwnerContext;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

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

    @Override
    public Multimap map(Object sourceMap_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);
        Multimap sourceMultimap = toNotNullMultimap(sourceMap_);
        Multimap targetMultimap = ArrayListMultimap.create();

        MapEnumerationOwnerContext enumeratorContext = new MapEnumerationOwnerContext(owner, true);

        Collection<Map.Entry<?, ?>> entries = sourceMultimap.entries();
        for (Map.Entry<?, ?> entry : entries){
            //key
            enumeratorContext.switchToKey();
            Object mappedKey = mapFunction.apply(entry.getKey(), enumeratorContext);

            //value
            enumeratorContext.switchToValue(mappedKey);
            Object value = entry.getValue();
            Object mappedValue = mapFunction.apply(value, enumeratorContext);

            targetMultimap.put(mappedKey, mappedValue);
        }
        return Multimaps.unmodifiableMultimap(targetMultimap);
    }

    @Override
    public boolean isEmpty(Object container){
        return container == null || ((Multimap) container).isEmpty();
    }
}
