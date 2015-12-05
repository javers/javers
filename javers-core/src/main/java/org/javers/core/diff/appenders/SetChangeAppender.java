package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.javers.common.collections.Objects.nullSafeEquals;

/**
 * @author pawel szymczyk
 */
class SetChangeAppender extends CorePropertyChangeAppender<SetChange> {

    private static final Logger logger = LoggerFactory.getLogger(SetChangeAppender.class);

    private final TypeMapper typeMapper;

    private final GlobalIdFactory globalIdFactory;

    SetChangeAppender(TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof SetType;
    }

    //TODO add support for ValueObjects
    private boolean isSupportedContainer(Property property) {
        ContainerType propertyType = typeMapper.getPropertyType(property);

        if (typeMapper.isValueObject(propertyType.getItemType())) {
            logger.error("could not diff " + property +", "+
                         JaversExceptionCode.SET_OF_VO_DIFF_NOT_IMPLEMENTED.getMessage() );
            return false;
        }
        return true;
    }

    private List<ContainerElementChange> calculateEntryChanges(SetType setType, Set leftRawSet, Set rightRawSet, OwnerContext owner) {

        JaversType itemType = typeMapper.getJaversType(setType.getItemType());
        DehydrateContainerFunction dehydrateFunction = new DehydrateContainerFunction(itemType, globalIdFactory);

        if (nullSafeEquals(leftRawSet, rightRawSet)) {
            return Collections.EMPTY_LIST;
        }

        Set<GlobalId> leftSet = (Set) setType.map(leftRawSet, dehydrateFunction, owner);
        Set<GlobalId> rightSet = (Set) setType.map(rightRawSet, dehydrateFunction, owner);

        List<ContainerElementChange> changes = new ArrayList<>();

        for (Object globalCdoId : Sets.difference(leftSet, rightSet)) {
            changes.add(new ValueRemoved(globalCdoId));
        }
        for (Object globalCdoId : Sets.difference(rightSet, leftSet)) {
            changes.add(new ValueAdded(globalCdoId));
        }

        return changes;
    }

    @Override
    public SetChange calculateChanges(NodePair pair, Property property) {
        Set leftValues = (Set) pair.getLeftPropertyValue(property);
        Set rightValues = (Set) pair.getRightPropertyValue(property);

        if (!isSupportedContainer(property)) {
            return null; //TODO ADD SUPPORT
        }

        SetType setType = typeMapper.getPropertyType(property);
        OwnerContext owner = new OwnerContext(pair.getGlobalId(), property.getName());
        List<ContainerElementChange> entryChanges =
                calculateEntryChanges(setType, leftValues, rightValues, owner);

        if (!entryChanges.isEmpty()) {
            return new SetChange(pair.getGlobalId(), property.getName(), entryChanges);
        } else {
            return null;
        }
    }
}
