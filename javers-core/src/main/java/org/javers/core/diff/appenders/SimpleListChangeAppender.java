package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class SimpleListChangeAppender extends ListToMapAppenderAdapter {

    SimpleListChangeAppender(MapChangeAppender mapChangeAppender) {
        super(mapChangeAppender);
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, JaversProperty property) {
        List leftList = (List) leftValue;
        List rightList = (List) rightValue;

        return super.calculateChangesInList(leftList, rightList, pair, property);
    }
}
