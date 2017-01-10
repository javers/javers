package org.javers.core.metamodel.type;

import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

/**
 * @author bartosz.walacik
 */
public class MapEnumerationOwnerContext extends EnumerationAwareOwnerContext {
    private Object key;
    private boolean isKey;

    public MapEnumerationOwnerContext(OwnerContext ownerContext) {
        super(ownerContext, false);
    }

    public MapEnumerationOwnerContext(OwnerContext ownerContext, boolean requiresObjectHasher) {
        super(ownerContext, requiresObjectHasher);
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

    public void switchToValue(Object key) {
        this.key = key;
        this.isKey = false;
    }

    public void switchToKey() {
        this.key = null;
        this.isKey = true;
    }
}
