package org.javers.core.metamodel.property;

/**
 * @author bartosz walacik
 */
public class PropertyScannerEntity {

    private int privateProperty = 1;
    protected int protectedProperty = 1;
    int packagePrivateProperty = 1;
    public int publicProperty = 1;

    private int getPrivateProperty() {
        return privateProperty;
    }

    protected int getProtectedProperty() {
        return protectedProperty;
    }

    int getPackagePrivateProperty() {
        return packagePrivateProperty;
    }

    public int getPublicProperty() {
        return publicProperty;
    }
}
