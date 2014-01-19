package org.javers.core.diff.changetype.map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class EntryRemoved extends EntryAddOrRemove {

    public EntryRemoved(Object key, Object value) {
        super(key, value);
    }
}
