package org.javers.guava.multimap;

import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;

/**
 * @author akrystian
 */
public class MultimapChange extends MapChange{
    public MultimapChange(GlobalId affectedCdoId, String propertyName, List<EntryChange> changes){
        super(affectedCdoId, propertyName, changes);
    }
}