package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ValueAdded;
import org.javers.core.diff.changetype.ValueRemoved;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.SetType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.javers.common.collections.Collections.difference;

public class SetChangeAppender extends PropertyChangeAppender<PropertyChange>{

    private TypeMapper typeMapper;

    public SetChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return SetType.class;
    }

    @Override
    protected Collection<PropertyChange> calculateChanges(NodePair pair, Property property) {
        Collection leftValues = (Collection) pair.getLeftPropertyValue(property);
        Collection rightValues = (Collection) pair.getRightPropertyValue(property);

        Set<PropertyChange> changes = new HashSet<>();

        if (typeMapper.isCollectionOfEntityReferences(property)) {
            throw new IllegalArgumentException();
        } else {
            for (Object addedValue : difference(rightValues, leftValues)) {
                changes.add(new ValueAdded(pair.getGlobalCdoId(), property, addedValue));
            }

            for (Object addedValue : difference(leftValues, rightValues)) {
                changes.add(new ValueRemoved(pair.getGlobalCdoId(), property, addedValue));
            }
        }
        return changes;
    }
}
