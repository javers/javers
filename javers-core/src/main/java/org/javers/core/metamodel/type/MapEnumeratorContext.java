package org.javers.core.metamodel.type;

import org.javers.core.metamodel.object.EnumeratorContext;

/**
* @author bartosz walacik
*/
public class MapEnumeratorContext implements EnumeratorContext {
    private Object key;
    private boolean isKey;

    @Override
    public String getPath() {
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
