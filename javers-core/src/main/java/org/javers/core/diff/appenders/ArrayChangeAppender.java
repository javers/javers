package org.javers.core.diff.appenders;

import java.util.Collections;
import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.metamodel.type.ArrayType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;

import java.util.List;
import java.util.Map;

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppender implements PropertyChangeAppender<ArrayChange>{
    private final MapChangeAppender mapChangeAppender;

    ArrayChangeAppender(MapChangeAppender mapChangeAppender) {
        this.mapChangeAppender = mapChangeAppender;
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

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(leftMap, rightMap, arrayType.getItemJaversType());

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new ArrayChange(pair.createPropertyChangeMetadata(property), elementChanges,
                pair.getLeftPropertyValueAndSanitize(property),
                pair.getRightPropertyValueAndSanitize(property));
        }
        else {
            return null;
        }
    }
}
