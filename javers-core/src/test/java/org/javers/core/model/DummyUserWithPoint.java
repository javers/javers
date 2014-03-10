package org.javers.core.model;

/**
 * @author bartosz walacik
 */
public class DummyUserWithPoint {
    private final DummyPoint point;

    private DummyUserWithPoint(int x, int y) {
        this.point = new DummyPoint(x,y);
    }

    public static DummyUserWithPoint userWithPoint(int x, int y) {
        return new DummyUserWithPoint(x,y);
    }

    DummyPoint getPoint() {
        return point;
    }
}
