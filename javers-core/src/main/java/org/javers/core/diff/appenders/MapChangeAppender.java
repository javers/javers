package org.javers.core.diff.appenders;

import org.javers.common.collections.Maps;
import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.map.*;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.MapType;

import java.util.*;

import static org.javers.common.collections.Objects.nullSafeEquals;

/**
 * @author bartosz walacik
 */
public class MapChangeAppender  extends PropertyChangeAppender<MapChange> {

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return MapType.class;
    }

    @Override
    protected Collection<MapChange> calculateChanges(NodePair pair, Property property) {
        Map leftMap =  (Map)pair.getLeftPropertyValue(property);
        Map rightMap = (Map)pair.getRightPropertyValue(property);

        if (nullSafeEquals(leftMap, rightMap)) {
            return Collections.EMPTY_SET;
        }

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Maps.commonKeys(leftMap, rightMap)) {
            Object leftVal  = leftMap.get(commonKey);
            Object rightVal = rightMap.get(commonKey);

            if (!nullSafeEquals(leftVal, rightVal)){
                changes.add( new EntryValueChanged(commonKey, leftVal, rightVal));
            }
        }

        for (Object addedKey : Maps.keysDifference(rightMap,leftMap)) {
            Object addedValue  = rightMap.get(addedKey);
            changes.add( new EntryAdded(addedKey, addedValue));
        }

        for (Object removedKey : Maps.keysDifference(leftMap, rightMap)) {
            Object removedValue  = leftMap.get(removedKey);
            changes.add( new EntryRemoved(removedKey, removedValue));
        }

        if (changes.size()>0) {
            return Sets.asSet(new MapChange(pair.getGlobalCdoId(), property, changes));
        }
        else {
            return Collections.EMPTY_SET;
        }
    }
}
