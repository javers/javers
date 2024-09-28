package org.javers.spring.boot.mongo;

import org.javers.core.metamodel.annotation.Id;

/**
 * @author bbrakefieldmn
 */
public class DummyEntityWithDouble {

    @Id
    private final int id;
    private final double doubleValue;

    public DummyEntityWithDouble(int id, double doubleValue) {
        this.id = id;
        this.doubleValue = doubleValue;
    }

    @Id
    public int getId() {
        return id;
    }

    public double getDoubleValue() {return doubleValue;}
}
