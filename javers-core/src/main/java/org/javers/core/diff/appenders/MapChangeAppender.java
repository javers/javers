package org.javers.core.diff.appenders;

import org.javers.common.collections.Maps;
import org.javers.common.exception.JaversException;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.metamodel.type.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY;
import static org.javers.core.diff.appenders.CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded;

/**
 * @author bartosz walacik
 */
class MapChangeAppender implements PropertyChangeAppender<MapChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        if (!(propertyType instanceof MapType)){
            return false;
        }

        MapType mapType = (MapType)propertyType;
        if (mapType.getKeyJaversType() instanceof ValueObjectType){
            throw new JaversException(VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY, propertyType);
        }

        return true;
    }

    @Override
    public MapChange calculateChanges(NodePair pair, JaversProperty property) {
        MapType mapType = property.getType();

        Map left =  wrapKeysIfNeeded((Map) pair.getLeftDehydratedPropertyValueAndSanitize(property), mapType.getKeyJaversType());
        Map right = wrapKeysIfNeeded((Map) pair.getRightDehydratedPropertyValueAndSanitize(property), mapType.getKeyJaversType());

        List<EntryChange> changes = calculateEntryChanges(left, right, mapType.getValueJaversType());

        if (!changes.isEmpty()){
            renderNotParametrizedWarningIfNeeded(mapType.getKeyJavaType(), "key", "Map", property);
            renderNotParametrizedWarningIfNeeded(mapType.getValueJavaType(), "value", "Map", property);
            return new MapChange(pair.createPropertyChangeMetadata(property), changes,
                    (Map)pair.getLeftPropertyValueAndSanitize(property),
                    (Map)pair.getRightPropertyValueAndSanitize(property));
        }
        else {
            return null;
        }
    }

    private Map wrapKeysIfNeeded(Map map, JaversType mapKeyType) {
        return HashWrapper.wrapKeysIfNeeded(map, mapKeyType);
    }

    /**
     * @return never returns null
     */
    List<EntryChange> calculateEntryChanges(Map leftMap, Map rightMap, JaversType mapValueType) {

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Maps.commonKeys(leftMap, rightMap)) {
            Object leftVal  = leftMap.get(commonKey);
            Object rightVal = rightMap.get(commonKey);

            if (!mapValueType.equals(leftVal, rightVal)){
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
