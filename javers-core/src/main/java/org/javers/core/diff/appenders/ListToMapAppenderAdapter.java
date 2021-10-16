package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.type.*;

import java.util.List;

import static org.javers.common.collections.Lists.asMap;

abstract class ListToMapAppenderAdapter extends CorePropertyChangeAppender<ListChange> {
    private final MapChangeAppender mapChangeAppender;

    ListToMapAppenderAdapter(MapChangeAppender mapChangeAppender) {
        this.mapChangeAppender = mapChangeAppender;
    }

    ListChange calculateChangesInList(List leftList, List rightList, NodePair pair, JaversProperty property) {
        CollectionType listType = property.getType();

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(asMap(leftList), asMap(rightList), listType.getItemJaversType());

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            renderNotParametrizedWarningIfNeeded(listType.getItemJavaType(), "item", "List", property);
            return new ListChange(pair.createPropertyChangeMetadata(property), elementChanges, leftList, rightList);
        }
        else {
            return null;
        }
    }
}
