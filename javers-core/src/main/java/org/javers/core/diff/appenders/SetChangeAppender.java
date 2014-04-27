package org.javers.core.diff.appenders;

import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.javers.common.collections.Collections.difference;

/**
 * @author pawel szymczyk
 */
public class SetChangeAppender extends PropertyChangeAppender<SetChange>{

    private static final Logger logger = LoggerFactory.getLogger(SetChangeAppender.class);

    private final TypeMapper typeMapper;

    public SetChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    protected boolean supports(JaversType propertyType) {
        return  propertyType instanceof SetType;
    }

    //TODO add support for Entities & ValueObjects
    public boolean isSupportedContainer(Property property) {
        ContainerType propertyType = typeMapper.getPropertyType(property);

        if (! typeMapper.isPrimitiveOrValue(propertyType.getItemClass())){
            logger.error(JaversExceptionCode.DIFF_NOT_IMPLEMENTED.getMessage() +" on "+property);
            return false;
        }
        return true;
    }

    @Override
    protected SetChange calculateChanges(NodePair pair, Property property) {
        Set leftValues =  (Set) pair.getLeftPropertyValue(property);
        Set rightValues = (Set) pair.getRightPropertyValue(property);

        List<ContainerElementChange> changes = new ArrayList<>();

        for (Object addedValue : difference(rightValues, leftValues)) {
            changes.add(new ValueAdded(addedValue));
        }

        for (Object addedValue : difference(leftValues, rightValues)) {
            changes.add(new ValueRemoved(addedValue));
        }

        if (changes.isEmpty()) {
            return null;
        }

        if (!isSupportedContainer(property)){
            return null; //TODO ADD SUPPORT
        }

        return new SetChange(pair.getGlobalCdoId(), property, changes);
    }
}
