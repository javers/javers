package org.javers.core.diff.appenders;

import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;

public abstract class ListChangeAppender extends CorePropertyChangeAppender<ListChange> {
    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }
}
