package org.javers.core.model;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.EmbeddedId;

public class DummyCompositePoint {

    @EmbeddedId
    private DummyPoint dummyPoint;

    private int value;

    public DummyCompositePoint(DummyPoint dummyPoint, int value) {
        this.dummyPoint = dummyPoint;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DummyCompositePoint && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public int getValue() {
        return value;
    }

    public DummyPoint getDummyPoint() {
        return dummyPoint;
    }
}
