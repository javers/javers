package org.javers.core

import com.google.common.collect.Multimap
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.property.Property;

/**
 * This is a Fake object not a real comparator
 *
 * @author bartosz walacik
 */
class CustomMultimapFakeComparator implements CustomPropertyComparator<Multimap, MapChange>{
    @Override
    Optional<MapChange> compare(Multimap left, Multimap right, GlobalId affectedId, Property property) {
        return Optional.of(new MapChange(affectedId, property.name, [new EntryValueChange("a", left.get("a")[0], right.get("a")[0])]));
    }
}
