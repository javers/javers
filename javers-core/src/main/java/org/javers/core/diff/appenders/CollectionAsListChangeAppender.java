package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;

import java.util.Collection;
import java.util.List;

class CollectionAsListChangeAppender extends ListToMapAppenderAdapter  {

    CollectionAsListChangeAppender(MapChangeAppender mapChangeAppender) {
        super(mapChangeAppender);
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType.getClass() == CollectionType.class;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, JaversProperty property) {
        List leftList = Lists.immutableListOf((Collection)leftValue);
        List rightList = Lists.immutableListOf((Collection)rightValue);

        return super.calculateChangesInList(leftList, rightList, pair, property);
    }
}
