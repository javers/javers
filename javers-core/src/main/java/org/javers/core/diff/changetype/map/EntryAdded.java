package org.javers.core.diff.changetype.map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class EntryAdded extends EntryAddOrRemove {

    public EntryAdded(Object key, Object value) {
        super(key, value);
    }
}
