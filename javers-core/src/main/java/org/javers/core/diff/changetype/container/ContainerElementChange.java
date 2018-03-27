package org.javers.core.diff.changetype.container;

import org.javers.common.string.PrettyValuePrinter;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ContainerElementChange) {
            ContainerElementChange that = (ContainerElementChange) obj;
            return Objects.equals(this.getIndex(), that.getIndex());
        }
        return false;
    }

    protected abstract String prettyPrint(PrettyValuePrinter valuePrinter);

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIndex());
    }
}
