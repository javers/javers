package org.javers.core.metamodel.object;

import org.javers.common.collections.Arrays;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.*;

import java.util.LinkedList;

/**
 * @author bartosz.walacik
 */
class GlobalIdPathParser {

    private final TypeMapper typeMapper;

    public GlobalIdPathParser(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    ValueObjectType parseChildValueObject(ManagedType ownerType, String path){
        return parseChildValueObjectFromPathSegments(ownerType, pathToSegments(path), path);
    }

    private ValueObjectType parseChildValueObjectFromPathSegments(ManagedType ownerType, LinkedList<String> segments, String path) {
        JaversProperty property = ownerType.getProperty(segments.getFirst());

        ValueObjectType childVoType = extractChildValueObject(property.getType(), path);

        if (segments.size() == 1 ||
            segments.size() == 2 &&  property.getType() instanceof EnumerableType){
            return childVoType;
        }

        segments.removeFirst();
        if (property.getType() instanceof EnumerableType){
            segments.removeFirst(); //removing segment with list index or map key
        }

        return parseChildValueObjectFromPathSegments(childVoType, segments, path);
    }

    private ValueObjectType extractChildValueObject(JaversType voPropertyType, String path) {

        if (voPropertyType instanceof ValueObjectType) {
            return (ValueObjectType) voPropertyType;
        }

        if (voPropertyType instanceof ContainerType) {
            JaversType contentType  = typeMapper.getJaversType(((ContainerType) voPropertyType).getItemType());
            if (contentType instanceof ValueObjectType){
                return (ValueObjectType)contentType;
            }
        }

        if (voPropertyType instanceof MapType){
            JaversType valueType  = typeMapper.getJaversType(((MapType) voPropertyType).getValueType());
            if (valueType instanceof ValueObjectType){
                return (ValueObjectType)valueType;
            }
        }

        throw new JaversException(JaversExceptionCode.CANT_EXTRACT_CHILD_VALUE_OBJECT,
                path, voPropertyType);

    }

    private LinkedList<String> pathToSegments(String path){
        Validate.argumentIsNotNull(path);
        return new LinkedList(Arrays.asList(path.split("/")));
    }
}
