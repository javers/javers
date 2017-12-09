package org.javers.core.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author bartosz walacik
 */
public class DummyPoint {

    private final int x;
    private final int y;

    public DummyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DummyPoint && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    public String getStringId() {
        return "("+ x + "," + y + ")";
    }
}
