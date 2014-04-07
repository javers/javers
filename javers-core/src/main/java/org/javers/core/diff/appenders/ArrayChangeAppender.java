package org.javers.core.diff.appenders;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ArrayChange;
import org.javers.core.diff.changetype.ContainerValueChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author pawel szymczyk
 */
public class ArrayChangeAppender extends PropertyChangeAppender<ArrayChange>{

    private static final Logger logger = LoggerFactory.getLogger(ArrayChangeAppender.class);

    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    public ArrayChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return ArrayType.class;
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
    protected ArrayChange calculateChanges(NodePair pair, Property property) {

        Map leftMap =  Arrays.asMap(pair.getLeftPropertyValue(property));
        Map rightMap = Arrays.asMap(pair.getRightPropertyValue(property));

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(leftMap, rightMap);

        if (!entryChanges.isEmpty()){
            if (!isSupportedContainer(property)){
                return null; //TODO ADD SUPPORT
            }

            List<ContainerValueChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());

            return new ArrayChange(pair.getGlobalCdoId(), property, elementChanges);
        }
        else {
            return null;
        }
    }
}
