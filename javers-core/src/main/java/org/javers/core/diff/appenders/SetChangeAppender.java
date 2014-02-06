package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ContainerValueChange;
import org.javers.core.diff.changetype.ElementAdded;
import org.javers.core.diff.changetype.ElementRemoved;
import org.javers.core.diff.changetype.SetChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.SetType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.javers.common.collections.Collections.difference;

/**
 * @author pawel szymczyk
 */
public class SetChangeAppender extends PropertyChangeAppender<SetChange>{

    private static final Logger logger = LoggerFactory.getLogger(SetChangeAppender.class);

    private TypeMapper typeMapper;

    public SetChangeAppender(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return SetType.class;
    }

    //TODO
    @Override
    protected boolean supports(JaversType propertyType) {
        if (!super.supports(propertyType)) {
            return false;
        }

        boolean isSupported = typeMapper.isSupportedContainer((SetType) propertyType);

        if (!isSupported) {
            logger.warn("unsupported set content type [{}], skipping", propertyType.getBaseJavaType());
        }

        return isSupported;
    }

    @Override
    protected Collection<SetChange> calculateChanges(NodePair pair, Property property) {
        Collection leftValues = (Collection) pair.getLeftPropertyValue(property);
        Collection rightValues = (Collection) pair.getRightPropertyValue(property);

        List<ContainerValueChange> changes = new ArrayList<>();

        for (Object addedValue : difference(rightValues, leftValues)) {
            changes.add(new ElementAdded(addedValue));
        }

        for (Object addedValue : difference(leftValues, rightValues)) {
            changes.add(new ElementRemoved(addedValue));
        }

        if (changes.isEmpty()) {
            return java.util.Collections.EMPTY_SET;
        }

        return Sets.asSet(new SetChange(pair.getGlobalCdoId(), property, changes));
    }
}
