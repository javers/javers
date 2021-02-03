package org.javers.core.metamodel.scanner;

class PropertyScannerEntity {
    private int privateProperty = 1
    protected int protectedProperty = 1
    int packagePrivateProperty = 1
    int publicProperty = 1

    private int getPrivateProperty() {
        return privateProperty
    }

    protected int getProtectedProperty() {
        return protectedProperty
    }

    int getPackagePrivateProperty() {
        return packagePrivateProperty
    }

    int getPublicProperty() {
        return publicProperty
    }
}
