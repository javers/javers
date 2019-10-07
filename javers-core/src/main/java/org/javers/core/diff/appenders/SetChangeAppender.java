package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.metamodel.type.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pawel szymczyk
 */
class SetChangeAppender extends CorePropertyChangeAppender<SetChange> {
    private final TypeMapper typeMapper;

    SetChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof SetType;
    }

    @Override
    protected SetChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, JaversProperty property) {
        Set leftSet = wrapHashIfNeeded((Set) leftValue, property);
        Set rightSet = wrapHashIfNeeded((Set) rightValue, property);

        List<ContainerElementChange> entryChanges = calculateDiff(leftSet, rightSet);
        if (!entryChanges.isEmpty()) {
            CollectionType setType = property.getType();
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "Set", property);
            return new SetChange(pair.createPropertyChangeMetadata(property), entryChanges);
        } else {
            return null;
        }
    }

    //TODO move
    Set wrapHashIfNeeded(Set set, JaversProperty property) {
        JaversType itemType = typeMapper.getContainerItemType(property);

        if (itemType instanceof CustomComparableType && ((CustomComparableType) itemType).hasCustomValueComparator()) {
            CustomComparableType customType = (CustomComparableType) itemType;
            return (Set)set.stream()
                            .map(it -> new HashWrapper(it, itemType::equals, customType::valueToString))
                            .collect(Collectors.toSet());
        }

        return set;
    }

    private List<ContainerElementChange> calculateDiff(Set leftSet, Set rightSet) {
        if (Objects.equals(leftSet, rightSet)) {
            return Collections.emptyList();
        }

        List<ContainerElementChange> changes = new ArrayList<>();

        Sets.difference(leftSet, rightSet).forEach(valueOrId -> changes.add(new ValueRemoved(valueOrId)));

        Sets.difference(rightSet, leftSet).forEach(valueOrId -> changes.add(new ValueAdded(valueOrId)));

        return changes;
    }
}
