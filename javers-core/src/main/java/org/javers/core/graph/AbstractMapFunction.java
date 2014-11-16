package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
public abstract class AbstractMapFunction implements EnumerableFunction{
    private final EnumerableType enumerableType;
    private final TypeMapper typeMapper;

    protected AbstractMapFunction(EnumerableType enumerableType, TypeMapper typeMapper) {
        Validate.argumentsAreNotNull(enumerableType, typeMapper);
        this.enumerableType = enumerableType;
        this.typeMapper = typeMapper;
    }

    protected MapType getMapType() {
        return (MapType)enumerableType;
    }

    protected boolean isMap(){
        return enumerableType instanceof MapType;
    }

    protected EnumerableType getEnumerableType() {
        return enumerableType;
    }

    protected TypeMapper getTypeMapper() {
        return typeMapper;
    }

    /**
     * @throws JaversException VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    protected <T extends JaversType> T getKeyType(){
        JaversType keyType = typeMapper.getJaversType(getMapType().getKeyClass());

        //corner case for Map<ValueObject,?>
        if (keyType instanceof ValueObjectType) {
            throw new JaversException(JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY,
                    getMapType().getKeyClass().getName(),
                    getMapType().getBaseJavaType().toString());
        }

        return (T) keyType;
    }

    /**
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    protected <T extends JaversType> T getValueType(){
        return (T)typeMapper.getJaversType(getMapType().getValueClass());
    }
}
