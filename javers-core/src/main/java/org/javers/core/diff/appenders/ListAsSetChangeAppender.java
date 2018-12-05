package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListAsSetType;

import java.util.Collection;

/**
 * @author Sergey Kobyshev
 */
public class ListAsSetChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final SetChangeAppender setChangeAppender;

    ListAsSetChangeAppender(SetChangeAppender setChangeAppender) {
        this.setChangeAppender = setChangeAppender;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListAsSetType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {
        SetChange setChange = setChangeAppender.calculateChanges(leftValue, rightValue, affectedId, property);
        if (setChange != null) {
            return new ListChange(affectedId, property.getName(), setChange.getChanges());
        }
        return null;
    }
}
