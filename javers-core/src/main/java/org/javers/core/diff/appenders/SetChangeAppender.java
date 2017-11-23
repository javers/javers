package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.metamodel.object.DehydrateContainerFunction;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.SetType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.*;


/**
 * @author pawel szymczyk
 */
class SetChangeAppender extends CorePropertyChangeAppender<SetChange> {
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

    List<ContainerElementChange> calculateEntryChanges(SetType setType, Set leftRawSet, Set rightRawSet, OwnerContext owner) {

        JaversType itemType = typeMapper.getJaversType(setType.getItemType());
        DehydrateContainerFunction dehydrateFunction = new DehydrateContainerFunction(itemType, globalIdFactory);

        if (Objects.equals(leftRawSet, rightRawSet)) {
            return Collections.emptyList();
        }

        Set leftSet = (Set) setType.map(leftRawSet, dehydrateFunction, owner);
        Set rightSet = (Set) setType.map(rightRawSet, dehydrateFunction, owner);

        List<ContainerElementChange> changes = new ArrayList<>();

        Sets.difference(leftSet, rightSet).forEach(valueOrId ->
                changes.add(new ValueRemoved(valueOrId)));

        Sets.difference(rightSet, leftSet).forEach(valueOrId ->
                changes.add(new ValueAdded(valueOrId)));

        return changes;
    }

    @Override
    public SetChange calculateChanges(NodePair pair, JaversProperty property) {
        Set leftValues = (Set) pair.getLeftPropertyValue(property);
        Set rightValues = (Set) pair.getRightPropertyValue(property);

        SetType setType = property.getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());
        List<ContainerElementChange> entryChanges =
                calculateEntryChanges(setType, leftValues, rightValues, owner);

        if (!entryChanges.isEmpty()) {
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "Set", property);
            return new SetChange(pair.getGlobalId(), property.getName(), entryChanges);
        } else {
            return null;
        }
    }
}
