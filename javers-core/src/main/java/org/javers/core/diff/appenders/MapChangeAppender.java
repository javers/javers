package org.javers.core.diff.appenders;

import org.javers.common.collections.Maps;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.map.*;
import org.javers.core.diff.custom.CustomComparators;
import org.javers.core.diff.custom.CustomToNativeAppenderAdapter;
import org.javers.core.metamodel.object.DehydrateMapFunction;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.*;

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY;

/**
 * @author bartosz walacik
 */
class MapChangeAppender extends CorePropertyChangeAppender<MapChange> {

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;
    private final CustomComparators customComparators;

    MapChangeAppender(TypeMapper typeMapper, GlobalIdFactory globalIdFactory, CustomComparators customComparators) {
        Validate.argumentsAreNotNull(typeMapper, globalIdFactory);
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
        this.customComparators = customComparators;
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
    public MapChange calculateChanges(NodePair pair, Property property) {
        Map leftRawMap =  (Map)pair.getLeftPropertyValue(property);
        Map rightRawMap = (Map)pair.getRightPropertyValue(property);

        MapType mapType = ((JaversProperty) property).getType();
        MapContentType mapContentType = typeMapper.getMapContentType(mapType);

        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());
        List<EntryChange> changes = calculateEntryChanges(pair, property, leftRawMap, rightRawMap, owner, mapContentType);

        if (!changes.isEmpty()){
            renderNotParametrizedWarningIfNeeded(mapContentType.getKeyType().getBaseJavaType(), "key", "Map", property);
            renderNotParametrizedWarningIfNeeded(mapContentType.getValueType().getBaseJavaType(), "value", "Map", property);
            return new MapChange(pair.getGlobalId(), property.getName(), changes);
        }
        else {
            return null;
        }
    }

    /**
     * @return never returns null
     */
    List<EntryChange> calculateEntryChanges(NodePair pair, Property property, Map leftRawMap, Map rightRawMap, OwnerContext owner, MapContentType mapContentType) {

        DehydrateMapFunction dehydrateFunction = new DehydrateMapFunction(globalIdFactory, mapContentType);

        Map leftMap =  MapType.mapStatic(leftRawMap, dehydrateFunction, owner);
        Map rightMap = MapType.mapStatic(rightRawMap, dehydrateFunction, owner);

        if (Objects.equals(leftMap, rightMap)) {
            return Collections.emptyList();
        }

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Maps.commonKeys(leftMap, rightMap)) {
            Object leftVal = leftMap.get(commonKey);
            Object rightVal = rightMap.get(commonKey);

            Optional<CustomToNativeAppenderAdapter<?, ValueChange>> customComparator =
                    customComparators.valueChangeCustomComparatorForClass(leftVal.getClass());

            if (customComparator.isPresent()) {
                addCustomValueChange(pair, property, changes, commonKey, customComparator);
            } else if (!Objects.equals(leftVal, rightVal)) {
                changes.add(new EntryValueChange(commonKey, leftVal, rightVal));
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

    private void addCustomValueChange(NodePair pair, Property property, List<EntryChange> changes, Object commonKey, Optional<CustomToNativeAppenderAdapter<?, ValueChange>> customComparator) {
        ValueChange customValueChange = customComparator.get().calculateChanges(pair, property);
        if (customValueChange != null) {
            changes.add(new EntryValueChange(commonKey, customValueChange.getLeft(), customValueChange.getRight()));
        }
    }
}
