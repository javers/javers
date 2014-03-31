package org.javers.core.metamodel.object;

/**
 * @author bartosz walacik
 */
public class MapOwnerContext extends OwnerContext {
    private String key;
    private boolean isKey;

    MapOwnerContext(GlobalCdoId owner, String propertyName) {
        super(owner, propertyName);
    }

    public MapOwnerContext(OwnerContext owner) {
        super(owner.getGlobalCdoId(), owner.getPropertyName());
    }

    @Override
    public String getPath() {
        if (key != null) {
            return super.getPath() + "/" + key;
        }
        return super.getPath();
    }

    public void switchToValue(String key) {
        this.key = key;
        this.isKey = false;
    }

    public boolean isKey() {
        return isKey;
    }

    public void switchToKey() {
        this.key = null;
        this.isKey = true;
    }
}
