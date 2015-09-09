package org.javers.core.model;

import javax.persistence.EmbeddedId;

/**
 * @author bartosz.walacik
 */
public class DummyEntityWithEmbeddedId {
    @EmbeddedId
    private DummyPoint point;

    private int someVal;

    @EmbeddedId
    public DummyPoint getPoint() {
        return point;
    }

    public int getSomeVal() {
        return someVal;
    }
}
