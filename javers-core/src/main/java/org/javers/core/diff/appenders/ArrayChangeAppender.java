package org.javers.core.diff.appenders;

import org.javers.common.collections.Arrays;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ArrayType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.Collection;
import java.util.List;


public class ArrayChangeAppender extends PropertyChangeAppender<PropertyChange>{

    private ListChangeAppender listChangeAppender;

    public ArrayChangeAppender(ListChangeAppender listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return ArrayType.class;
    }

    @Override
    protected Collection<PropertyChange> calculateChanges(NodePair pair, Property property) {
        List left = Arrays.asList(pair.getLeftPropertyValue(property));
        List right = Arrays.asList(pair.getRightPropertyValue(property));

        return listChangeAppender.calculateChanges(pair.getGlobalCdoId(), property, left, right);
    }
}
