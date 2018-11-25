package org.javers.core.diff.appenders;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.object.GlobalId;
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
    public ArrayChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {

        Map leftMap =  Arrays.asMap(leftValue);
        Map rightMap = Arrays.asMap(rightValue);

        ArrayType arrayType = property.getType();
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());
        MapContentType mapContentType = typeMapper.getMapContentType(arrayType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(leftMap, rightMap, owner, mapContentType);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new ArrayChange(affectedId, property.getName(), elementChanges);
        }
        else {
            return null;
        }
    }
}
