package org.javers.model.mapping.type;

import org.javers.common.validation.Validate;

/**
 * Map content type.
 * <p/>
 *
 * Holds actual key and unwrap Class arguments of Map Type
 *
 * @author bartosz walacik
 */
public class EntryClass {
    private final Class key;
    private final Class value;

    public EntryClass(Class key, Class value) {
        Validate.argumentsAreNotNull(key, value);
        this.key = key;
        this.value = value;
    }

    public Class getKey() {
        return key;
    }

    public Class getValue() {
        return value;
    }
}
