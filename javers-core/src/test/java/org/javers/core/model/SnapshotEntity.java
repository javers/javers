package org.javers.core.model;

import org.joda.time.LocalDate;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private Set<Integer> setOfIntegers;
    private Set<LocalDate> setOfDates;
    private Set<SnapshotEntity> setOfEntities;
    private Set<DummyAddress> setOfValueObjects;


    private Map<String, Integer> mapOfPrimitives;
    private Map<LocalDate,BigDecimal> mapOfValues;
    private Map<String,DummyAddress> mapPrimitiveToVO;
    private Map<SnapshotEntity, SnapshotEntity> mapOfEntities;
    private Map<DummyAddress,String> mapVoToPrimitive;           //not supported
    private Map nonParametrizedMap;                              //not supported

    public int getId() {
        return id;
    }
}
