package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Sets;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.metamodel.object.DehydrateMapFunction;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.javers.common.collections.Objects.nullSafeEquals;

/**
 * @author pawel szymczyk
 */
public class SetChangeAppender extends PropertyChangeAppender<SetChange> {

    private static final Logger logger = LoggerFactory.getLogger(SetChangeAppender.class);

    private final TypeMapper typeMapper;

    private final MapChangeAppender mapChangeAppender;

    private final GlobalIdFactory globalIdFactory;

    public SetChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        this.typeMapper = typeMapper;
        this.mapChangeAppender = mapChangeAppender;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    protected boolean supports(JaversType propertyType) {
        return propertyType instanceof SetType;
    }

    //TODO add support for Entities & ValueObjects
    public boolean isSupportedContainer(Property property) {
        ContainerType propertyType = typeMapper.getPropertyType(property);

        if (! typeMapper.isPrimitiveOrValue(propertyType.getItemClass())){
            logger.error(JaversExceptionCode.DIFF_NOT_IMPLEMENTED.getMessage() +" on "+property);
            return false;
        }
        return true;
    }

    protected List<EntryChange> calculateEntryChanges(MapType mapType, Map leftRawMap, Map rightRawMap, OwnerContext owner) {

        DehydrateMapFunction dehydrateFunction = new DehydrateMapFunction(mapType, typeMapper, globalIdFactory);

        Map leftMap = mapType.map(leftRawMap, dehydrateFunction, owner);
        Map rightMap = mapType.map(rightRawMap, dehydrateFunction, owner);

        if (nullSafeEquals(leftMap, rightMap)) {
            return Collections.EMPTY_LIST;
        }

        List<EntryChange> changes = new ArrayList<>();

        Set leftSet = Sets.asSet(leftMap.values());
        Set rightSet = Sets.asSet(rightMap.values());

        for (Object key : leftMap.keySet()) {
            Object leftVal = leftMap.get(key);
            if (!rightSet.contains(leftVal)) {
                changes.add(new EntryRemoved(key, leftVal));
            }
        }

        for (Object key : rightMap.keySet()) {
            Object rightVal = rightMap.get(key);
            if (leftSet.contains(rightVal)) {
                changes.add(new EntryAdded(key, rightVal));
            }
        }

        return changes;
    }

    @Override
    protected SetChange calculateChanges(NodePair pair, Property property) {
        Set leftValues = (Set) pair.getLeftPropertyValue(property);
        Set rightValues = (Set) pair.getRightPropertyValue(property);

        if (!isSupportedContainer(property)) {
            return null; //TODO ADD SUPPORT
        }

        SetType setType = typeMapper.getPropertyType(property);
        OwnerContext owner = new OwnerContext(pair.getGlobalCdoId(), property.getName());
        List<EntryChange> entryChanges =
                calculateEntryChanges(new MapType(setType), Sets.asMap(leftValues), Sets.asMap(rightValues), owner);

        if (!entryChanges.isEmpty()) {
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new SetChange(pair.getGlobalCdoId(), property, elementChanges);
        } else {
            return null;
        }
    }
}
