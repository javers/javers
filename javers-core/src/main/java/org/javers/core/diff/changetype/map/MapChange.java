package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Changes on a Map property
 *
 * @author bartosz walacik
 */
public class MapChange<T extends Map<?,?>> extends KeyValueChange<T> {

    public MapChange(PropertyChangeMetadata metadata, List<EntryChange> changes, T left, T right) {
        super(metadata, changes, left, right);
    }

    public MapChange(PropertyChangeMetadata metadata, List<EntryChange> changes) {
        super(metadata, changes, (T)Collections.emptyMap(), (T)Collections.emptyMap());
    }
}
