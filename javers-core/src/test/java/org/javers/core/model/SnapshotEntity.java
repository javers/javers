package org.javers.core.model;

import org.joda.time.LocalDate;
import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class SnapshotEntity {
    @Id
    private int id = 1;

    private LocalDate dob;

    private int intProperty;

    private SnapshotEntity entityRef;

    private DummyAddress valueObjectRef;

    private Integer[] arrayOfIntegers;

    private LocalDate[] arrayOfDates;

    public int getId() {
        return id;
    }

    public LocalDate getDob() {
        return dob;
    }

    public SnapshotEntity getEntityRef() {
        return entityRef;
    }

    public DummyAddress getValueObjectRef() {
        return valueObjectRef;
    }
}
