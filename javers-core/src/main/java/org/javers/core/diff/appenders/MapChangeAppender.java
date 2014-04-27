package org.javers.core.diff.appenders;

import com.sun.xml.internal.bind.v2.TODO;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Maps;
import org.javers.common.collections.Sets;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.ValueObject;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.MapType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.metamodel.type.ValueObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.javers.common.collections.Objects.nullSafeEquals;

/**
 * @author bartosz walacik
 */
public class MapChangeAppender  extends PropertyChangeAppender<MapChange> {
    private static final Logger logger = LoggerFactory.getLogger(MapChangeAppender.class);

    private final TypeMapper typeMapper;

    public MapChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    protected boolean supports(JaversType propertyType) {
        return propertyType instanceof MapType;
    }

    private void isSupportedMap(Property property){
        MapType mapType = typeMapper.getPropertyType(property);

        JaversType keyType = typeMapper.getJaversType(mapType.getKeyClass());
        if (keyType instanceof ValueObjectType){
            /** TODO code repetition
             *  see {@link org.javers.core.graph.AbstractMapFunction#getKeyType()} */
            throw new JaversException(JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY,
                    mapType.getKeyClass().getName(),
                    mapType.getBaseJavaType().toString());
        }
    }

    @Override
    protected MapChange calculateChanges(NodePair pair, Property property) {
        Map leftMap =  (Map)pair.getLeftPropertyValue(property);
        Map rightMap = (Map)pair.getRightPropertyValue(property);

        List<EntryChange> changes = calculateEntryChanges(leftMap, rightMap);

        if (!changes.isEmpty()){
            isSupportedMap(property);

            return new MapChange(pair.getGlobalCdoId(), property, changes);
        }
        else {
            return null;
        }
    }

    /**
     * @return never returns null
     */
    List<EntryChange> calculateEntryChanges(Map leftMap, Map rightMap) {
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
