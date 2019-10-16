package org.javers.core.diff.appenders;

import org.javers.common.collections.Maps;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.metamodel.type.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY;
import static org.javers.core.diff.appenders.CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded;

/**
 * @author bartosz walacik
 */
class MapChangeAppender implements PropertyChangeAppender<MapChange> {

    private final TypeMapper typeMapper;

    MapChangeAppender(TypeMapper typeMapper) {
        Validate.argumentsAreNotNull(typeMapper);
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        if (!(propertyType instanceof MapType)){
            return false;
        }

        MapContentType mapContentType = typeMapper.getMapContentType((MapType)propertyType);
        if (mapContentType.getKeyType() instanceof ValueObjectType){
            throw new JaversException(VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY, propertyType);
        }

        return true;
    }

    @Override
    public MapChange calculateChanges(NodePair pair, JaversProperty property) {

        MapType mapType = ((JaversProperty) property).getType();
        MapContentType mapContentType = typeMapper.getMapContentType(mapType);

        Map left =  wrapKeysIfNeeded((Map) pair.getLeftDehydratedPropertyValueAndSanitize(property), mapContentType);
        Map right = wrapKeysIfNeeded((Map) pair.getRightDehydratedPropertyValueAndSanitize(property), mapContentType);

        List<EntryChange> changes = calculateEntryChanges(left, right, mapContentType);

        if (!changes.isEmpty()){
            renderNotParametrizedWarningIfNeeded(mapContentType.getKeyType().getBaseJavaType(), "key", "Map", property);
            renderNotParametrizedWarningIfNeeded(mapContentType.getValueType().getBaseJavaType(), "value", "Map", property);
            return new MapChange(pair.createPropertyChangeMetadata(property), changes);
        }
        else {
            return null;
        }
    }

    private Map wrapKeysIfNeeded(Map map, MapContentType mapContentType) {
        return HashWrapper.wrapKeysIfNeeded(map, mapContentType.getKeyType());
    }

    /**
     * @return never returns null
     */
    List<EntryChange> calculateEntryChanges(Map leftMap, Map rightMap, MapContentType mapContentType) {

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Maps.commonKeys(leftMap, rightMap)) {
            Object leftVal  = leftMap.get(commonKey);
            Object rightVal = rightMap.get(commonKey);

            if (!mapContentType.getValueType().equals(leftVal, rightVal)){
                changes.add( new EntryValueChange(commonKey, leftVal, rightVal));
            }
        }

        for (Object addedKey : Maps.keysDifference(rightMap, leftMap)) {
            Object addedValue  = rightMap.get(addedKey);
            changes.add( new EntryAdded(addedKey, addedValue));
        }

        for (Object removedKey : Maps.keysDifference(leftMap, rightMap)) {
            Object removedValue  = leftMap.get(removedKey);
            changes.add( new EntryRemoved(removedKey, removedValue));
        }

        return changes;
    }
}
