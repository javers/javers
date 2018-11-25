package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

class CollectionAsListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final ListChangeAppender listChangeAppender;

    CollectionAsListChangeAppender(ListChangeAppender listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType.getClass() == CollectionType.class;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {
        List leftList = Lists.immutableListOf((Collection)leftValue);
        List rightList = Lists.immutableListOf((Collection)rightValue);

        return listChangeAppender.calculateChanges(leftList, rightList, affectedId, property);
    }
}
