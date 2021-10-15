package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.metamodel.type.*;

import java.util.*;

/**
 * @author pawel szymczyk
 */
class SetChangeAppender extends CorePropertyChangeAppender<SetChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof SetType;
    }

    @Override
    protected SetChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, JaversProperty property) {
        Set leftSet = wrapValuesIfNeeded(toSet(leftValue), property);
        Set rightSet = wrapValuesIfNeeded(toSet(rightValue), property);

        List<ContainerElementChange> entryChanges = calculateDiff(leftSet, rightSet);
        if (!entryChanges.isEmpty()) {
            CollectionType setType = property.getType();
            renderNotParametrizedWarningIfNeeded(setType.getItemJavaType(), "item", "Set", property);
            return new SetChange(pair.createPropertyChangeMetadata(property), entryChanges,
                new Atomic(toSet(leftValue)), new Atomic(toSet(rightValue)));
        } else {
            return null;
        }
    }

    private Set wrapValuesIfNeeded(Set set, JaversProperty property) {
        JaversType itemType = ((ContainerType)property.getType()).getItemJaversType();
        return HashWrapper.wrapValuesIfNeeded(set, itemType);
    }

    private Set toSet(Object collection) {
        if (collection instanceof Set) {
            return (Set) collection;
        }
        return new HashSet((Collection)collection);
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
