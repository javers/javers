package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListAsSetType;

/**
 * @author Sergey Kobyshev
 */
public class ListAsSetChangeAppender implements PropertyChangeAppender<ListChange> {

    private final SetChangeAppender setChangeAppender;

    ListAsSetChangeAppender(SetChangeAppender setChangeAppender) {
        this.setChangeAppender = setChangeAppender;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListAsSetType;
    }

    @Override
    public ListChange calculateChanges(NodePair pair, JaversProperty property) {
        SetChange setChange = setChangeAppender.calculateChanges(pair, property);

        if (setChange != null) {
            return new ListChange(pair.createPropertyChangeMetadata(property), setChange.getChanges());
        }
        return null;
    }
}
