package org.javers.core.diff.appenders;

import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ArrayChange;
import org.javers.core.diff.changetype.ContainerValueChange;
import org.javers.core.diff.changetype.ListChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalCdoId;
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
import java.util.Map;

/**
 * @author pawel szymczyk
 */
public class ArrayChangeAppender extends PropertyChangeAppender<ArrayChange>{

    private static final Logger logger = LoggerFactory.getLogger(ArrayChangeAppender.class);

    private final MapChangeAppender mapChangeAppender;
    private final TypeMapper typeMapper;

    public ArrayChangeAppender(MapChangeAppender mapChangeAppender, TypeMapper typeMapper) {
        this.mapChangeAppender = mapChangeAppender;
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
            logger.warn("unsupported Array content type [{}], skipping", propertyType.getBaseJavaType());
        }

        return isSupported;
    }

    @Override
    protected Collection<ArrayChange> calculateChanges(NodePair pair, Property property) {

        Map leftMap =  Arrays.asMap(pair.getLeftPropertyValue(property));
        Map rightMap = Arrays.asMap(pair.getRightPropertyValue(property));

        GlobalCdoId id = pair.getGlobalCdoId();

        Collection<MapChange> mapChanges =
                mapChangeAppender.calculateChanges(id, property, leftMap, rightMap);

        if (mapChanges.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        List<ContainerValueChange> elementChanges = Lists.transform(mapChanges.iterator().next().getEntryChanges(), new MapChangesToListChangesFunction());

        ArrayChange arrayChange = new ArrayChange(pair.getGlobalCdoId(), property, elementChanges);

        return Sets.asSet(arrayChange);
    }
}
