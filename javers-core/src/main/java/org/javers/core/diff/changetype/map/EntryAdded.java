package org.javers.core.diff.changetype.map;

import static org.javers.common.string.ToStringBuilder.format;

/**
 * @author bartosz walacik
 */
public class EntryAdded extends EntryAddOrRemove {

    public EntryAdded(Object key, Object value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return format(getKey()) + " : " + format(getValue()) + " added";
    }
}
