package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.metamodel.type.*;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author Sergey Kobyshev
 */
public class ListAsSetChangeAppender implements PropertyChangeAppender<ListChange> {

    private final TypeMapper typeMapper;
    private final SetChangeAppender setChangeAppender;

    public ListAsSetChangeAppender(TypeMapper typeMapper, SetChangeAppender setChangeAppender) {
        this.typeMapper = typeMapper;
        this.setChangeAppender = setChangeAppender;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListAsSetType;
    }

    @Override
    public ListChange calculateChanges(NodePair pair, JaversProperty property) {
        SetChange setChange = null;

        ListAsSetType listType = property.getType();
        JaversType itemType = typeMapper.getJaversType(listType.getItemType());
        if (itemType instanceof CustomType) {
            CustomType ct = (CustomType)itemType;
            List leftList= (List)pair.getLeftDehydratedPropertyValueAndSanitize(property);
            List rightList = (List)pair.getRightDehydratedPropertyValueAndSanitize(property);

            List<ContainerElementChange> entryChanges = calculateDiff(leftList, rightList, (a,b) -> ct.equals(a,b));
            if (entryChanges.size() > 0) {
                return new ListChange(pair.createPropertyChangeMetadata(property), entryChanges);
            }
        }
        else {
            setChange = setChangeAppender.calculateChanges(pair, property);
            if (setChange != null) {
                return new ListChange(pair.createPropertyChangeMetadata(property), setChange.getChanges());
            }
        }
        return null;
    }

    private List<ContainerElementChange> calculateDiff(List leftList, List rightList, BiFunction<?,?, Boolean> equalsFunction) {

        List<ContainerElementChange> changes = new ArrayList<>();

        Lists.difference(leftList, rightList, (BiFunction) equalsFunction)
                .forEach(valueOrId -> changes.add(new ValueRemoved(valueOrId)));


        Lists.difference(rightList, leftList, (BiFunction)equalsFunction)
                .forEach(valueOrId -> changes.add(new ValueAdded(valueOrId)));

        return changes;
    }
}
