package org.javers.core.model;

/**
 * @author bartosz walacik
 */
public class DummyUserWithPoint {
    private DummyPoint point;

    public DummyUserWithPoint(DummyPoint point) {
        this.point = point;
    }

    public DummyUserWithPoint(int x, int y) {
        this.point = new DummyPoint(x,y);
    }

    public static DummyUserWithPoint userWithPoint(int x, int y) {
        return new DummyUserWithPoint(x,y);
    }

    public DummyPoint getPoint() {
        return point;
    }
}
