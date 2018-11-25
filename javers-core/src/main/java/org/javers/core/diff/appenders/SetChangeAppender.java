package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.changetype.container.*;
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

    List<ContainerElementChange> calculateEntryChanges(CollectionType setType, Collection leftRawSet, Collection rightRawSet, OwnerContext owner) {

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
    public SetChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {
        Collection leftValues = (Collection) leftValue;
        Collection rightValues = (Collection) rightValue;

        CollectionType setType = property.getType();
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());
        List<ContainerElementChange> entryChanges =
                calculateEntryChanges(setType, leftValues, rightValues, owner);

        if (!entryChanges.isEmpty()) {
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "Set", property);
            return new SetChange(affectedId, property.getName(), entryChanges);
        } else {
            return null;
        }
    }
}
