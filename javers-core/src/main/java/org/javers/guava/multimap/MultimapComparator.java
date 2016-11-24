package org.javers.guava.multimap;

import com.google.common.collect.Multimap;
import org.javers.common.collections.Collections;
import org.javers.common.collections.Sets;
import org.javers.common.exception.JaversException;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY;

/**
 * Compares Guava Multimaps.
 * <br/>
 *
 * It's automatically registered, if Guava is detected on a classpath.
 *
 * @author akrystian
 */
public class MultimapComparator extends CorePropertyChangeAppender implements CustomPropertyComparator<Multimap, MapChange>{
    private static final Logger logger = LoggerFactory.getLogger(MultimapComparator.class);

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    public MultimapComparator(TypeMapper typeMapper, GlobalIdFactory globalIdFactory){
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    private boolean isSupportedContainer(Property property){
        JaversType propertyType = typeMapper.getPropertyType(property);
        if (typeMapper.isValueObject(((MultimapType) propertyType).getKeyType())) {
            logger.error(new JaversException(VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY, propertyType).getMessage());
            return false;
        }
        return true;
    }

    @Override
    public MapChange compare(Multimap left, Multimap right, GlobalId affectedId, Property property){
        if (!isSupportedContainer(property) || left.equals(right)){
            return null;
        }

        MultimapType multimapType = typeMapper.getPropertyType(property);
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());

        List<EntryChange> entryChanges = calculateEntryChanges(multimapType, left, right, owner);
        if (!entryChanges.isEmpty()){
            renderNotParametrizedWarningIfNeeded(multimapType.getKeyType(), "key", "Map", property);
            renderNotParametrizedWarningIfNeeded(multimapType.getValueType(), "value", "Map", property);
            return new MapChange(affectedId, property.getName(), entryChanges);
        }else{
            return null;
        }
    }

    private List<EntryChange> calculateEntryChanges(MultimapType multimapType, Multimap left, Multimap right, OwnerContext owner){
        DehydrateMapFunction dehydrateFunction = getDehydrateMapFunction(multimapType);
        Multimap leftMultimap = multimapType.map(left, dehydrateFunction, owner);
        Multimap rightMultimap = multimapType.map(right, dehydrateFunction, owner);

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Multimaps.commonKeys(leftMultimap, rightMultimap)){
            Set leftValues = (Set)leftMultimap.get(commonKey);
            Set rightValues =(Set) rightMultimap.get(commonKey);
            final Collection difference = Sets.xor(leftValues, rightValues);
            if (difference.size() > 0){
                calculateValueChanges(changes, commonKey, leftValues, rightValues);
            }
        }

        calculateKeyChanges(leftMultimap, rightMultimap, changes);

        return changes;
    }

    private void calculateKeyChanges(Multimap leftMultimap, Multimap rightMultimap, List<EntryChange> changes){
        for (Object addedKey : Multimaps.keysDifference(rightMultimap, leftMultimap)){
            Collection difference = Collections.difference(rightMultimap.get(addedKey), leftMultimap.get(addedKey));
            for (Object addedValue : difference){
                changes.add(new EntryAdded(addedKey, addedValue));
            }
        }
        for (Object removedKey : Multimaps.keysDifference(leftMultimap, rightMultimap)){
            for (Object removedValue : Collections.difference(leftMultimap.get(removedKey), rightMultimap.get(removedKey))){
                changes.add(new EntryRemoved(removedKey, removedValue));
            }
        }
    }

    private void calculateValueChanges(List<EntryChange> changes, Object commonKey, Collection leftVal, Collection rightVal){
        if (Objects.equals(leftVal, rightVal)){
            return;
        }
        final Collection valuesRemoved = Collections.difference(leftVal, rightVal);
        if(valuesRemoved.size() > 0){
            for (Object addedValue : valuesRemoved){
                changes.add( new EntryRemoved(commonKey, addedValue));
            }
        }
        final Collection valuesAdded = Collections.difference(rightVal, leftVal);
        if(valuesAdded.size() > 0){
            for (Object addedValue : valuesAdded){
                changes.add( new EntryAdded(commonKey, addedValue));
            }
        }
    }

    private DehydrateMapFunction getDehydrateMapFunction(MultimapType multimapType){
        JaversType keyType = typeMapper.getJaversType(multimapType.getKeyType());
        JaversType valueType = typeMapper.getJaversType(multimapType.getValueType());

        MultiMapContentType multiMapContentType = new MultiMapContentType(keyType, valueType);
        return new DehydrateMapFunction(globalIdFactory, multiMapContentType);
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return  propertyType instanceof MultimapType;
    }

    @Override
    public PropertyChange calculateChanges(NodePair pair, Property supportedProperty){
        return null;//TODO
    }
}
