package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.*;

import java.util.*;

import static org.javers.core.diff.appenders.CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded;

/**
 * @author pawel szymczyk
 */
class SetChangeAppender implements PropertyChangeAppender<SetChange> {
    private final TypeMapper typeMapper;

    SetChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof SetType;
    }

    @Override
    public SetChange calculateChanges(NodePair pair, JaversProperty property) {
        GlobalId affectedId = pair.getGlobalId();

        Set leftSet = toSet(pair.getLeftDehydratedPropertyValueAndSanitize(property));
        Set rightSet = toSet(pair.getRightDehydratedPropertyValueAndSanitize(property));

        List<ContainerElementChange> entryChanges = calculateDiff(leftSet, rightSet);
        if (!entryChanges.isEmpty()) {
            CollectionType setType = property.getType();
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "Set", property);
            return new SetChange(affectedId, property.getName(), entryChanges);
        } else {
            return null;
        }
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
