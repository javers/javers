package org.javers.core.diff.changetype.map;

/**
 * @author bartosz walacik
 */
public class EntryRemoved extends EntryAddOrRemove {

    public EntryRemoved(Object key, Object value) {
        super(key, value);
    }
}
