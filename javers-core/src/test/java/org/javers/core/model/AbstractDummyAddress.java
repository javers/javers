package org.javers.core.model;

/**
 * @author pawel szymczyk
 */
abstract class AbstractDummyAddress {

    private int inheritedInt;

    public int getInheritedInt() {
        return inheritedInt;
    }

    public void setInheritedInt(int inheritedInt) {
        this.inheritedInt = inheritedInt;
    }
}
