package org.javers.guava;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Compares Guava Multisets.
 * <br/>
 *
 * It's automatically registered, if Guava is detected on the classpath.
 *
 * @author akrystian
 */
class MultisetChangeAppender extends CorePropertyChangeAppender<SetChange> {

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;


    MultisetChangeAppender(TypeMapper typeMapper, GlobalIdFactory globalIdFactory){
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return  propertyType instanceof MultisetType;
    }

    @Override
    public SetChange calculateChanges(NodePair pair, JaversProperty property){
        Multiset left =  (Multiset)pair.getLeftPropertyValue(property);
        Multiset right = (Multiset)pair.getRightPropertyValue(property);

        MultisetType multisetType = ((JaversProperty) property).getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());

        List<ContainerElementChange> entryChanges = calculateEntryChanges(multisetType, left, right, owner);
        if (!entryChanges.isEmpty()){
            renderNotParametrizedWarningIfNeeded(multisetType.getItemType(), "item", "Multiset", property);
            return new SetChange(pair.getGlobalId(), property.getName(), entryChanges);
        } else {
            return null;
        }
    }

    private List<ContainerElementChange> calculateEntryChanges(MultisetType multisetType, Multiset left, Multiset right,  OwnerContext owner){
        JaversType itemType = typeMapper.getJaversType(multisetType.getItemType());
        DehydrateContainerFunction dehydrateFunction = new DehydrateContainerFunction(itemType, globalIdFactory);

        Multiset leftMultiset = (Multiset) multisetType.map(left,dehydrateFunction,owner);
        Multiset rightMultiset = (Multiset) multisetType.map(right,dehydrateFunction,owner);

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
}
