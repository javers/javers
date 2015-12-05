package org.javers.core.metamodel.object;

import org.javers.common.collections.Arrays;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.LinkedList;

/**
 * @author bartosz.walacik
 */
class GlobalIdPathParser {

    private final TypeMapper typeMapper;
    private final String path;

    public GlobalIdPathParser(String path, TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
        this.path = path;
    }

    ValueObjectType parseChildValueObject(ManagedType ownerType){
        return parseChildValueObjectFromPathSegments(ownerType, pathToSegments(path));
    }

    private ValueObjectType parseChildValueObjectFromPathSegments(ManagedType ownerType, LinkedList<String> segments) {
        Property property = ownerType.getProperty(segments.getFirst());
        JaversType propertyType = typeMapper.getJaversType(property.getGenericType());

        ValueObjectType childVoType = extractChildValueObject(propertyType);

        if (segments.size() == 1 ||
            segments.size() == 2 &&  propertyType instanceof EnumerableType){
            return childVoType;
        }

        segments.removeFirst();
        if (propertyType instanceof EnumerableType){
            segments.removeFirst(); //removing segment with list index or map key
        }

        return parseChildValueObjectFromPathSegments(childVoType, segments);
    }

    private ValueObjectType extractChildValueObject(JaversType voPropertyType) {

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
