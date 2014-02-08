package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Maps;
import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.MapType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.javers.common.collections.Objects.nullSafeEquals;

/**
 * @author bartosz walacik
 */
public class MapChangeAppender  extends PropertyChangeAppender<MapChange> {
    private static final Logger logger = LoggerFactory.getLogger(MapChangeAppender.class);

    private TypeMapper typeMapper;

    public MapChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    protected boolean supports(JaversType propertyType) {
        if (!super.supports(propertyType)) {
            return false;
        }

        MapType mapType = (MapType)propertyType;
        boolean isSupported = typeMapper.isSupportedMap(mapType);

        if (!isSupported) {
            logger.warn("unsupported map content type [{}], skipping", propertyType.getBaseJavaType());
        }

        return isSupported;
    }

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return MapType.class;
    }

    @Override
    protected MapChange calculateChanges(NodePair pair, Property property) {
        Map leftMap =  (Map)pair.getLeftPropertyValue(property);
        Map rightMap = (Map)pair.getRightPropertyValue(property);

        List<EntryChange> changes = calculateEntryChanges(leftMap, rightMap);

        if (!changes.isEmpty()){
            return new MapChange(pair.getGlobalCdoId(), property, changes);
        }
        else {
            return null;
        }
    }

    /**
     * @return never returns null
     */
    protected List<EntryChange> calculateEntryChanges(Map leftMap, Map rightMap) {
        if (nullSafeEquals(leftMap, rightMap)) {
            return Collections.EMPTY_LIST;
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

        return changes;
    }

}
