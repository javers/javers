package org.javers.guava;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;

import java.util.ArrayList;
import java.util.List;

import static org.javers.core.diff.appenders.CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded;

/**
 * Compares Guava Multisets.
 * <br/>
 *
 * It's automatically registered, if Guava is detected on the classpath.
 *
 * @author akrystian
 */
class MultisetChangeAppender implements PropertyChangeAppender<MultisetChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return  propertyType instanceof MultisetType;
    }

    @Override
    public MultisetChange calculateChanges(NodePair pair, JaversProperty property) {

        Multiset left = (Multiset) pair.getLeftDehydratedPropertyValueAndSanitize(property);
        Multiset right = (Multiset) pair.getRightDehydratedPropertyValueAndSanitize(property);

        MultisetType multisetType = property.getType();
        OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());

        List<ContainerElementChange> entryChanges = calculateEntryChanges(multisetType, left, right, owner);
        if (!entryChanges.isEmpty()){
            renderNotParametrizedWarningIfNeeded(multisetType.getItemJavaType(), "item", "Multiset", property);
            return new MultisetChange(pair.createPropertyChangeMetadata(property), entryChanges,
                (Multiset) pair.getLeftPropertyValue(property),
                (Multiset) pair.getRightPropertyValue(property));
        } else {
            return null;
        }
    }

    private List<ContainerElementChange> calculateEntryChanges(MultisetType multisetType, Multiset left, Multiset right,  OwnerContext owner){

        List<ContainerElementChange> changes = new ArrayList<>();
        for (Object globalCdoId : Multisets.difference(left, right)){
            changes.add(new ValueRemoved(globalCdoId));
        }
        Multiset difference = Multisets.difference(right, left);
        for (Object globalCdoId : difference){
            changes.add(new ValueAdded(globalCdoId));
        }
        return changes;
    }
}
