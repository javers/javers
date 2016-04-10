package org.javers.guava.multimap;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.metamodel.object.DehydrateMapFunction;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Compares Multimap.
 * <p>
 * It's automatically registered, if Guava Multiset and Multimap dependency will be detected on class path.
 *
 * @author akrystian
 */
public class MultimapComparator implements CustomPropertyComparator<Multimap, MultimapChange>{
    private static final Logger logger = LoggerFactory.getLogger(MultimapComparator.class);

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    public MultimapComparator(TypeMapper typeMapper, GlobalIdFactory globalIdFactory){
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    private boolean isSupportedContainer(Property property){
        JaversType propertyType = typeMapper.getPropertyType(property);
        if (typeMapper.isValueObject(((MultimapType) propertyType).getKeyType())){
            logger.error("could not diff " + property + ", " +
                    JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MULTIMAP_KEY.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public MultimapChange compare(Multimap left, Multimap right, GlobalId affectedId, Property property){
        if (!isSupportedContainer(property) || left.equals(right)){
            return null;
        }

        MultimapType multimapType = typeMapper.getPropertyType(property);
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());

        List<EntryChange> entryChanges = calculateEntryChanges(multimapType, left, right, owner);
        if (!entryChanges.isEmpty()){
            return new MultimapChange(affectedId, property.getName(), entryChanges);
        }else{
            return null;
        }
    }

    private List<EntryChange> calculateEntryChanges(MultimapType multisetType, Multimap left, Multimap right, OwnerContext owner){
        DehydrateMapFunction dehydrateFunction = getDehydrateMapFunction(multisetType);

        Multimap leftMultimap = (Multimap) multisetType.map(left, dehydrateFunction, owner);
        Multimap rightMultimap = (Multimap) multisetType.map(right, dehydrateFunction, owner);


        if (Objects.equals(leftMultimap, rightMultimap)){
            return Collections.EMPTY_LIST;
        }

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Multimaps.commonKeys(leftMultimap, rightMultimap)){
            Multiset leftVal = HashMultiset.create(leftMultimap.get(commonKey));
            Multiset rightVal = HashMultiset.create(rightMultimap.get(commonKey));

            if (!Objects.equals(leftVal, rightVal)){
                calculateValueChanges(changes, commonKey, leftVal, rightVal);
            }
        }

        for (Object addedKey : Multimaps.keysDifference(rightMultimap, leftMultimap)){
            for (Object addedValue : rightMultimap.get(addedKey)){
                changes.add(new EntryAdded(addedKey, addedValue));
            }
        }

        for (Object removedKey : Multimaps.keysDifference(leftMultimap, rightMultimap)){
            for (Object removedValue : leftMultimap.get(removedKey)){
                changes.add(new EntryRemoved(removedKey, removedValue));
            }
        }

        return changes;
    }

    private void calculateValueChanges(List<EntryChange> changes, Object commonKey, Multiset leftVal, Multiset rightVal){
        for (Object addedValue : Multisets.difference(leftVal, rightVal)){
            changes.add(new EntryRemoved(commonKey, addedValue));
        }
        for (Object removedValue : Multisets.difference(rightVal, leftVal)){
            changes.add(new EntryAdded(commonKey, removedValue));
        }
    }

    private DehydrateMapFunction getDehydrateMapFunction(MultimapType multisetType){
        JaversType keyType = typeMapper.getJaversType(multisetType.getKeyType());
        JaversType valueType = typeMapper.getJaversType(multisetType.getValueType());

        MultiMapContentType multiMapContentType = new MultiMapContentType(keyType, valueType);
        return new DehydrateMapFunction(globalIdFactory, multiMapContentType);
    }
}