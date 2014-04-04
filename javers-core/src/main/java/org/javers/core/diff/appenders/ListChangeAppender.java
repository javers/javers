package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ContainerValueChange;
import org.javers.core.diff.changetype.ListChange;
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
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return ListType.class;
    }

    //TODO add support for Entities & ValueObjects
    public boolean isSupportedContainer(Property property) {
        ContainerType propertyType = typeMapper.getPropertyType(property);

        if (! typeMapper.isPrimitiveOrValueOrObject(propertyType.getItemClass())){
            logger.error(JaversExceptionCode.DIFF_NOT_IMPLEMENTED.getMessage() +" on "+property);
            return false;
        }
        return true;
    }

    @Override
    protected ListChange calculateChanges(final NodePair pair, final Property property) {
        List leftList = (List) pair.getLeftPropertyValue(property);
        List rightList = (List) pair.getRightPropertyValue(property);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(Lists.asMap(leftList), Lists.asMap(rightList));

        if (!entryChanges.isEmpty()){
            if (!isSupportedContainer(property)){
                return null; //TODO ADD SUPPORT
            }

            List<ContainerValueChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());

            return  new ListChange(pair.getGlobalCdoId(), property, elementChanges);
        }
        else {
            return null;
        }
    }
}
