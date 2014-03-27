package org.javers.core.model;

import org.joda.time.LocalDate;
import javax.persistence.Id;
import java.util.List;

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
    private SnapshotEntity[] arrayOfEntities;
    private DummyAddress[] arrayOfValueObjects;

    private List<Integer> listOfIntegers;
    private List<LocalDate> listOfDates;
    private List<SnapshotEntity> listOfEntities;
    private List<DummyAddress> listOfValueObjects;

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
