package org.javers.core.diff.appenders;

import java.util.Collections;
import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.type.*;

import java.util.List;
import java.util.Map;

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppender implements PropertyChangeAppender<ArrayChange>{
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

        Map leftMap =  Arrays.asMap(pair.getLeftDehydratedPropertyValueAndSanitize(property));
        Map rightMap = Arrays.asMap(pair.getRightDehydratedPropertyValueAndSanitize(property));

        ArrayType arrayType = property.getType();
        MapContentType mapContentType = typeMapper.getMapContentType(arrayType);

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(leftMap, rightMap, mapContentType);
        Object leftProperty = pair.getLeftDehydratedPropertyValueAndSanitize(property);
        Object rightProperty = pair.getRightDehydratedPropertyValueAndSanitize(property);

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new ArrayChange(pair.createPropertyChangeMetadata(property), elementChanges, new Atomic(leftProperty == null ? Collections.emptyList() : Arrays.asList(leftProperty)),
                new Atomic(rightProperty == null ? Collections.emptyList() : Arrays.asList(rightProperty)));
        }
        else {
            return null;
        }
    }
}
