package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

import java.util.List;

import static org.javers.common.collections.Lists.asMap;

/**
 * @author pawel szymczyk
 */
public class SimpleListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    SimpleListChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(final NodePair pair, final JaversProperty property) {
        List leftList = (List) pair.getLeftPropertyValue(property);
        List rightList = (List) pair.getRightPropertyValue(property);

        ListType listType = ((JaversProperty) property).getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());
        MapContentType mapContentType = typeMapper.getMapContentType(listType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(asMap(leftList), asMap(rightList), owner, mapContentType);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            renderNotParametrizedWarningIfNeeded(listType.getItemType(), "item", "List", property);
            return  new ListChange(pair.getGlobalId(), property.getName(), elementChanges);
        }
        else {
            return null;
        }
    }
}
