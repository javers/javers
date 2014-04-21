package org.javers.core.diff.changetype;

/**
 * @author pawel szymczyk
 */
public abstract class ContainerElementChange {
    private Integer index;

    ContainerElementChange(int index) {
        this.index = index;
    }

    ContainerElementChange() {
        this.index = null;
    }

    public Integer getIndex() {
        return index;
    }
}
