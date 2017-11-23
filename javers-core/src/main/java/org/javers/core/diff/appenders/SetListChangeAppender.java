package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

import java.util.List;

import static org.javers.common.collections.Sets.asSet;

/**
 * @author Sergey Kobyshev
 */
public class SetListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final SetChangeAppender setChangeAppender;
    private final TypeMapper typeMapper;

    SetListChangeAppender(SetChangeAppender setChangeAppender, TypeMapper typeMapper) {
        this.setChangeAppender = setChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(final NodePair pair, final JaversProperty property) {
        List leftList = (List) pair.getLeftPropertyValue(property);
        List rightList = (List) pair.getRightPropertyValue(property);

        ListType listType = ((JaversProperty) property).getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());
        SetType setType = typeMapper.getSetType(listType);

        List<ContainerElementChange> entryChanges =
                setChangeAppender.calculateEntryChanges(setType, leftList == null ? null : asSet(leftList), rightList == null ? null : asSet(rightList), owner);

        if (!entryChanges.isEmpty()) {
            renderNotParametrizedWarningIfNeeded(setType.getItemType(), "item", "Set", property);
            return new ListChange(pair.getGlobalId(), property.getName(), entryChanges);
        } else {
            return null;
        }
    }
}
