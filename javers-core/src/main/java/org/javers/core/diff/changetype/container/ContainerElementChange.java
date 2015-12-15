package org.javers.core.diff.changetype.container;

import java.io.Serializable;

/**
 * @author pawel szymczyk
 */
public abstract class ContainerElementChange implements Serializable {
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
