package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ListChangeAppender extends PropertyChangeAppender<ListChange> {

    private static final Logger logger = LoggerFactory.getLogger(ListChangeAppender.class);

    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    public ListChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    protected boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    protected ListChange calculateChanges(final NodePair pair, final Property property) {
        List leftList = (List) pair.getLeftPropertyValue(property);
        List rightList = (List) pair.getRightPropertyValue(property);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(Lists.asMap(leftList), Lists.asMap(rightList));

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return  new ListChange(pair.getGlobalCdoId(), property, elementChanges);
        }
        else {
            return null;
        }
    }
}
