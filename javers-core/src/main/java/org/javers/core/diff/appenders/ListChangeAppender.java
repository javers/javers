package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.javers.common.collections.Lists.asMap;

/**
 * @author pawel szymczyk
 */
class ListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private static final Logger logger = LoggerFactory.getLogger(ListChangeAppender.class);

    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    ListChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(final NodePair pair, final Property property) {
        List leftList = (List) pair.getLeftPropertyValue(property);
        List rightList = (List) pair.getRightPropertyValue(property);

        ListType listType = typeMapper.getPropertyType(property);
        OwnerContext owner = new OwnerContext(pair.getGlobalId(), property.getName());
        MapContentType mapContentType = typeMapper.getMapContentType(listType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(asMap(leftList), asMap(rightList), owner, mapContentType);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return  new ListChange(pair.getGlobalId(), property, elementChanges);
        }
        else {
            return null;
        }
    }
}
