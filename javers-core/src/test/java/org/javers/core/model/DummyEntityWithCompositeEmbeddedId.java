package org.javers.core.model;

import org.javers.core.metamodel.annotation.TypeName;

import javax.persistence.EmbeddedId;

/**
 * @author bartosz.walacik
 */
@TypeName("dummyEntityWithCompositeEmbeddedId")
public class DummyEntityWithCompositeEmbeddedId {

    @EmbeddedId
    private DummyCompositePoint point;

    private int someVal;

    @EmbeddedId
    public DummyCompositePoint getPoint() {
        return point;
    }

    public int getSomeVal() {
        return someVal;
    }
}
