package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;

class CollectionAsListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final ListChangeAppender listChangeAppender;

    CollectionAsListChangeAppender(ListChangeAppender listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof CollectionType;
    }

    @Override
    public ListChange calculateChanges(final NodePair pair, final JaversProperty property) {
        return null;
    }
}
