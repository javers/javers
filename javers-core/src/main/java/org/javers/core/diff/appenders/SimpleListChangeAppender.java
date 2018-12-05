package org.javers.core.diff.appenders;

import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class SimpleListChangeAppender extends ListToMapAppenderAdapter {

    SimpleListChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        super(mapChangeAppender, typeMapper);
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {
        List leftList = (List) leftValue;
        List rightList = (List) rightValue;

        return super.calculateChanges(leftList, rightList, affectedId, property);
    }
}
