package org.javers.core.diff.appenders;

import org.javers.common.collections.Maps;
import org.javers.common.validation.Validate;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.metamodel.object.DehydrateMapFunction;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.MapType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.javers.common.collections.Objects.nullSafeEquals;

/**
 * @author bartosz walacik
 */
public class MapChangeAppender  extends PropertyChangeAppender<MapChange> {
    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    public MapChangeAppender(TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        Validate.argumentsAreNotNull(typeMapper, globalIdFactory);
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    protected boolean supports(JaversType propertyType) {
        return propertyType instanceof MapType;
    }

    @Override
    protected MapChange calculateChanges(NodePair pair, Property property) {
        Map leftRawMap =  (Map)pair.getLeftPropertyValue(property);
        Map rightRawMap = (Map)pair.getRightPropertyValue(property);

        MapType mapType = typeMapper.getPropertyType(property);
        OwnerContext owner = new OwnerContext(pair.getGlobalId(), property.getName());
        List<EntryChange> changes = calculateEntryChanges(mapType, leftRawMap, rightRawMap, owner);

        if (!changes.isEmpty()){
            return new MapChange(pair.getGlobalId(), property, changes);
        }
        else {
            return null;
        }
    }

    /**
     * @return never returns null
     */
    List<EntryChange> calculateEntryChanges(MapType mapType, Map leftRawMap, Map rightRawMap, OwnerContext owner) {

        DehydrateMapFunction dehydrateFunction = new DehydrateMapFunction(mapType, typeMapper, globalIdFactory);

        Map leftMap =  mapType.map(leftRawMap, dehydrateFunction, owner);
        Map rightMap = mapType.map(rightRawMap, dehydrateFunction, owner);

        if (nullSafeEquals(leftMap, rightMap)) {
            return Collections.EMPTY_LIST;
        }

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Maps.commonKeys(leftMap, rightMap)) {
            Object leftVal  = leftMap.get(commonKey);
            Object rightVal = rightMap.get(commonKey);

            if (!nullSafeEquals(leftVal, rightVal)){
                changes.add( new EntryValueChange(commonKey, leftVal, rightVal));
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

        return changes;
    }

}
