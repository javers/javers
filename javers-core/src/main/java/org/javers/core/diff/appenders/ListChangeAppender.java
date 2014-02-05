package org.javers.core.diff.appenders;

import org.javers.common.collections.Collections;
import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;

import java.util.Collection;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ListChangeAppender extends PropertyChangeAppender<PropertyChange> {

    private final MapChangeAppender mapChangeAppender;

    public ListChangeAppender(MapChangeAppender mapChangeAppender) {
        this.mapChangeAppender = mapChangeAppender;
    }

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return ListType.class;
    }

    @Override
    protected Collection<PropertyChange> calculateChanges(final NodePair pair, final Property property) {
        List leftList = (List) pair.getLeftPropertyValue(property);
        List rightList = (List) pair.getRightPropertyValue(property);

        return calculateChanges(pair.getGlobalCdoId(), property, leftList, rightList);
    }

    protected Collection<PropertyChange> calculateChanges(final GlobalCdoId id, final Property property, List leftList, List rightList) {
        Collection<MapChange> mapChanges =
                mapChangeAppender.calculateChanges(id, property, Lists.asMap(leftList), Lists.asMap(rightList));

        if (mapChanges.isEmpty()) {
            return java.util.Collections.EMPTY_SET;
        }

        return Collections.transform(mapChanges.iterator().next().getEntryChanges(), new MapChangesToListChangesFunction(id, property));
    }
}
