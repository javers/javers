package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.*;

import java.util.*;

/**
 * @author pawel szymczyk
 */
public class SetChangeAppender extends CorePropertyChangeAppender<SetChange> {
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

    @Override
    public SetChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {

        CollectionType setType = property.getType();
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());

        JaversType itemType = typeMapper.getJaversType(setType.getItemType());
        DehydrateContainerFunction dehydrateFunction = new DehydrateContainerFunction(itemType, globalIdFactory);

        Set leftSet = (Set) setType.map(leftValue, dehydrateFunction, owner);
        Set rightSet = (Set) setType.map(rightValue, dehydrateFunction, owner);

        List<ContainerElementChange> entryChanges = calculateDiff(leftSet, rightSet);

        if (!entryChanges.isEmpty()) {
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "Set", property);
            return new SetChange(affectedId, property.getName(), entryChanges);
        } else {
            return null;
        }
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
