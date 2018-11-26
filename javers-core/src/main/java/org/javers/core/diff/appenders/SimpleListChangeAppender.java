package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.MapContentType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

import static org.javers.common.collections.Lists.asMap;

/**
 * @author pawel szymczyk
 */
public class SimpleListChangeAppender extends ListChangeAppender {

    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    SimpleListChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    public ListChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {
        List leftList = (List) leftValue;
        List rightList = (List) rightValue;

        CollectionType listType = ((JaversProperty) property).getType();
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());
        MapContentType mapContentType = typeMapper.getMapContentType(listType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(asMap(leftList), asMap(rightList), owner, mapContentType);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            renderNotParametrizedWarningIfNeeded(listType.getItemType(), "item", "List", property);
            return new ListChange(affectedId, property.getName(), elementChanges);
        }
        else {
            return null;
        }
    }
}
