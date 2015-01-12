package org.javers.core.diff.appenders;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ArrayType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.MapType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppender implements PropertyChangeAppender<ArrayChange>{

    private static final Logger logger = LoggerFactory.getLogger(ArrayChangeAppender.class);

    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    ArrayChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return  propertyType instanceof ArrayType;
    }

    @Override
    public ArrayChange calculateChanges(NodePair pair, Property property) {

        Map leftMap =  Arrays.asMap(pair.getLeftPropertyValue(property));
        Map rightMap = Arrays.asMap(pair.getRightPropertyValue(property));

        ArrayType arrayType = typeMapper.getPropertyType(property);
        OwnerContext owner = new OwnerContext(pair.getGlobalId(), property.getName());
        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(new MapType(arrayType), leftMap, rightMap, owner);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new ArrayChange(pair.getGlobalId(), property, elementChanges);
        }
        else {
            return null;
        }
    }
}
