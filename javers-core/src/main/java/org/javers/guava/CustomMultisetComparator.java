package org.javers.guava;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Compares Multiset.
 *
 * It's automatically registered, if Guava Multiset dependency will be detected on class path.
 *
 * @author akrystian
 */
public class CustomMultisetComparator implements CustomPropertyComparator<Multiset, SetChange>{

    @Override
    public SetChange compare(Multiset left, Multiset right, GlobalId affectedId, Property property){
        if (left.equals(right)){
            return null;
        }
        List<ContainerElementChange> changes = new ArrayList<>();

        for (Object globalCdoId : Multisets.difference(left, right)){
            changes.add(new ValueRemoved(globalCdoId));
        }
        Multiset difference = Multisets.difference(right, left);
        for (Object globalCdoId : difference){
            changes.add(new ValueAdded(globalCdoId));
        }

        return new SetChange(affectedId, property.getName(), changes);
    }
}
