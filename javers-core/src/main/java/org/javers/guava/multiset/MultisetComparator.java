package org.javers.guava.multiset;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.metamodel.object.DehydrateContainerFunction;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Compares Multisets.
 * <br/>
 *
 * It's automatically registered, if Guava is detected on the classpath.
 *
 * @author akrystian
 */
public class MultisetComparator extends CorePropertyChangeAppender implements CustomPropertyComparator<Multiset , SetChange> {

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;


    public MultisetComparator(TypeMapper typeMapper, GlobalIdFactory globalIdFactory){
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public SetChange compare(Multiset left, Multiset right, GlobalId affectedId, Property property){
        if (left.equals(right)){
            return null;
        }

        MultisetType multisetType = typeMapper.getPropertyType(property);
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());

        List<ContainerElementChange> entryChanges = calculateEntryChanges(multisetType, left, right, owner);
        if (!entryChanges.isEmpty()){
            renderNotParametrizedWarningIfNeeded(multisetType.getItemType(), "item", "Set", property);
            return new SetChange(affectedId, property.getName(), entryChanges);
        } else {
            return null;
        }
    }

    private List<ContainerElementChange> calculateEntryChanges(MultisetType multisetType, Multiset left, Multiset right,  OwnerContext owner){
        JaversType itemType = typeMapper.getJaversType(multisetType.getItemType());
        DehydrateContainerFunction dehydrateFunction = new DehydrateContainerFunction(itemType, globalIdFactory);

        Multiset<GlobalId> leftMultiset = (Multiset) multisetType.map(left,dehydrateFunction,owner);
        Multiset<GlobalId> rightMultiset = (Multiset) multisetType.map(right,dehydrateFunction,owner);

        List<ContainerElementChange> changes = new ArrayList<>();

        for (Object globalCdoId : Multisets.difference(leftMultiset, rightMultiset)){
            changes.add(new ValueRemoved(globalCdoId));
        }
        Multiset difference = Multisets.difference(rightMultiset, leftMultiset);
        for (Object globalCdoId : difference){
            changes.add(new ValueAdded(globalCdoId));
        }
        return changes;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return  propertyType instanceof MultisetType;
    }

    @Override
    public PropertyChange calculateChanges(NodePair pair, Property supportedProperty){
        return null; //TODO
    }
}
