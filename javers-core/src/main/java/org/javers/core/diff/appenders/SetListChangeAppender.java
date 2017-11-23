package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

import java.util.List;
import java.util.Set;

import static org.javers.common.collections.Sets.asSet;

/**
 * @author Sergey Kobyshev
 */
public class SetListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final SetChangeAppender setChangeAppender;

    SetListChangeAppender(SetChangeAppender setChangeAppender) {
        this.setChangeAppender = setChangeAppender;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(final NodePair pair, final JaversProperty property) {
        Set leftList =  asSet((List) pair.getLeftPropertyValue(property));
        Set rightList = asSet((List) pair.getRightPropertyValue(property));

        ListType listType = property.getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());
        SetType setType = new SetType(listType.getItemType());

        List<ContainerElementChange> entryChanges =
                setChangeAppender.calculateEntryChanges(setType, leftList, rightList, owner);

        if (!entryChanges.isEmpty()) {
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "List", property);
            return new ListChange(pair.getGlobalId(), property.getName(), entryChanges);
        } else {
            return null;
        }
    }
}
