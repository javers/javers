package org.javers.core.diff.appenders;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ArrayChange;
import org.javers.core.diff.changetype.ListChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ArrayType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ArrayChangeAppender extends PropertyChangeAppender<ArrayChange>{

    private static final Logger logger = LoggerFactory.getLogger(ArrayChangeAppender.class);

    private final ListChangeAppender listChangeAppender;
    private final TypeMapper typeMapper;

    public ArrayChangeAppender(ListChangeAppender listChangeAppender, TypeMapper typeMapper) {
        this.listChangeAppender = listChangeAppender;
        this.typeMapper = typeMapper;
    }

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return ArrayType.class;
    }

    //TODO
    @Override
    protected boolean supports(JaversType propertyType) {
        if (!super.supports(propertyType)) {
            return false;
        }

        boolean isSupported = typeMapper.isSupportedContainer((ListType) propertyType);

        if (!isSupported) {
            logger.warn("unsupported list content type [{}], skipping", propertyType.getBaseJavaType());
        }

        return isSupported;
    }

    @Override
    protected Collection<ArrayChange> calculateChanges(NodePair pair, Property property) {
        List left = Arrays.asList(pair.getLeftPropertyValue(property));
        List right = Arrays.asList(pair.getRightPropertyValue(property));

        Collection<ListChange> listChanges = listChangeAppender.calculateChanges(pair.getGlobalCdoId(), property, left, right);

        if (listChanges.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        ArrayChange arrayChange = new ArrayChange(pair.getGlobalCdoId(), property, listChanges.iterator().next().getChanges());

        return Sets.asSet(arrayChange);
    }
}
