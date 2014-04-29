package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Maps;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.*;

import static org.javers.common.exception.exceptions.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;

/**
 * @author bartosz walacik
 */
public class MapType extends EnumerableType {
    private transient List<Class> elementTypes;


    /**
     * Fake MapType for List & Array ChangeAppenders
     */
    public MapType(ContainerType containerType){
       super(containerType.getBaseJavaType());
       elementTypes = new ArrayList<>();
       elementTypes.add(Integer.class);//key
       elementTypes.add(containerType.getItemClass());//value
    }

    public MapType(Type baseJavaType) {
        super(baseJavaType);

        if (getActualClassTypeArguments().size() == 2) {
            elementTypes = Lists.immutableListOf(getActualClassTypeArguments().get(0), getActualClassTypeArguments().get(1));
        } else {
            elementTypes = Collections.EMPTY_LIST;
        }
    }

    @Override
    public boolean isFullyParametrized() {
        return elementTypes.size() == 2;
    }

    @Override
    public Map map(Object sourceMap_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);

        if (sourceMap_ == null) {
            return Collections.EMPTY_MAP;
        }

        Map<Object, Object> sourceMap = (Map) sourceMap_;
        Map<Object, Object> targetMap = new HashMap(sourceMap.size());

        MapEnumeratorContext enumeratorContext = new MapEnumeratorContext();
        owner.setEnumeratorContext(enumeratorContext);

        for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
            //key
            enumeratorContext.switchToKey();
            Object mappedKey = mapFunction.apply(entry.getKey(), owner);

            //value
            enumeratorContext.switchToValue(mappedKey);
            Object mappedValue = mapFunction.apply(entry.getValue(), owner);

            targetMap.put(mappedKey, mappedValue);
        }

        return Collections.unmodifiableMap(targetMap);
    }

    @Override
    public boolean isEmpty(Object map) {
        return map == null || ((Map)map).isEmpty();
    }

    /**
     * If both Key and Value type arguments are actual Classes,
     * returns List with key Class and value Class.
     * Otherwise returns empty List
     */
    @Override
    public List<Class> getElementTypes() {
        return elementTypes;
    }

    /**
     * never returns null
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Class getKeyClass() {
        if (isFullyParametrized()) {
            return elementTypes.get(0);
        }
        throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, getBaseJavaType().toString());
    }

    /**
     * never returns null
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public Class getValueClass() {
        if (isFullyParametrized()) {
            return elementTypes.get(1);
        }
        throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, getBaseJavaType().toString());
    }

}