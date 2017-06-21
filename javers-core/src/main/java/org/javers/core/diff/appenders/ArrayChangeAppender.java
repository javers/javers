package org.javers.core.diff.appenders;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

import java.util.List;
import java.util.Map;

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppender extends CorePropertyChangeAppender<ArrayChange>{
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
    public ArrayChange calculateChanges(NodePair pair, JaversProperty property) {

        Map leftMap =  Arrays.asMap(pair.getLeftPropertyValue(property));
        Map rightMap = Arrays.asMap(pair.getRightPropertyValue(property));

        ArrayType arrayType = ((JaversProperty) property).getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());
        MapContentType mapContentType = typeMapper.getMapContentType(arrayType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(leftMap, rightMap, owner, mapContentType);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new ArrayChange(pair.getGlobalId(), property.getName(), elementChanges);
        }
        else {
            return null;
        }
    }
}
