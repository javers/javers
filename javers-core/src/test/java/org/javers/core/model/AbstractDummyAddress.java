package org.javers.core.model;

/**
 * @author pawel szymczyk
 */
public abstract class AbstractDummyAddress {

    private int inheritedInt;

    public int getInheritedInt() {
        return inheritedInt;
    }

    public void setInheritedInt(int inheritedInt) {
        this.inheritedInt = inheritedInt;
    }
}
