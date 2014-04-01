package org.javers.core.metamodel.type;

import org.javers.core.metamodel.object.EnumeratorContext;

/**
* @author bartosz walacik
*/
public class MapEnumeratorContext implements EnumeratorContext {
    private String key;
    private boolean isKey;

    @Override
    public String getPath() {
        if (key != null) {
            return key;
        }
        return "";
    }

    public boolean isKey() {
        return isKey;
    }

    void switchToValue(String key) {
        this.key = key;
        this.isKey = false;
    }

    void switchToKey() {
        this.key = null;
        this.isKey = true;
    }
}
