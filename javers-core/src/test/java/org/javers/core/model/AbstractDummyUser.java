package org.javers.core.model;

/**
 * @author bartosz walacik
 */
public abstract class AbstractDummyUser {
    private int inheritedInt;

    public int getInheritedInt() {
        return inheritedInt;
    }

    public void setInheritedInt(int inheritedInt) {
        this.inheritedInt = inheritedInt;
    }
}
