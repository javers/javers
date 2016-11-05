package org.javers.guava.multimap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.MapEnumerationOwnerContext;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;



/**
 * @author akrystian
 */
public class MultimapType extends EnumerableType{

    public static MultimapType getInstance(){
        return new MultimapType(Multimap.class);
    }

    public MultimapType(Type baseJavaType) {
        super(baseJavaType, 2);
    }


    public static Multimap mapStatic(Object sourceMultimap_, EnumerableFunction mapFunction, OwnerContext owner){
        Validate.argumentIsNotNull(mapFunction);
        Multimap sourceMultimap = toNotNullMultimap(sourceMultimap_);
        Multimap targetMultimap = HashMultimap.create();

        MultimapEnumerationOwnerContext enumeratorContext = new MultimapEnumerationOwnerContext(owner);

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
    public Multimap map(Object sourceMap_, EnumerableFunction mapFunction, OwnerContext owner) {
        return mapStatic(sourceMap_, mapFunction, owner);
    }

    private static Multimap toNotNullMultimap(Object sourceMap){
        if (sourceMap == null){
            return HashMultimap.create();
        }else{
            return (HashMultimap) sourceMap;
        }
    }

    @Override
    public boolean isEmpty(Object container){
        return container == null || ((Multimap) container).isEmpty();
    }

    /**
     * never returns null
     *
     */
    public Type getKeyType(){
            return getConcreteClassTypeArguments().get(0);
    }

    /**
     * never returns null
     */
    public Type getValueType(){
            return getConcreteClassTypeArguments().get(1);
    }

    /**
     * marker class
     */
    public static class MultimapEnumerationOwnerContext extends MapEnumerationOwnerContext{
        MultimapEnumerationOwnerContext(OwnerContext ownerContext) {
            super(ownerContext);
        }
    }
}
