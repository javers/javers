package org.javers.core.metamodel.type;

import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

/**
 * @author bartosz.walacik
 */
public class MapEnumerationOwnerContext extends EnumerationAwareOwnerContext {
    private Object key;
    private boolean isKey;

    MapEnumerationOwnerContext(OwnerContext ownerContext) {
        super(ownerContext);
    }

    @Override
    public String getEnumeratorContextPath() {
        if (key != null) {
            return key.toString();
        }
        return "";
    }

    public boolean isKey() {
        return isKey;
    }

    void switchToValue(Object key) {
        this.key = key;
        this.isKey = false;
    }

    void switchToKey() {
        this.key = null;
        this.isKey = true;
    }
}
