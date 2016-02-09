package org.javers.core.model

import org.joda.time.LocalDate

import javax.persistence.Id

/**
 * @author bartosz walacik
 */
class SnapshotEntity {

    @Id
    int id = 1

    public enum DummyEnum { val1, val2, val3 }

    LocalDate dob

    int intProperty

    SnapshotEntity entityRef

    DummyAddress valueObjectRef

    Integer[] arrayOfIntegers
    int[] arrayOfInts
    LocalDate[] arrayOfDates
    SnapshotEntity[] arrayOfEntities
    DummyAddress[] arrayOfValueObjects

    List<Integer> listOfIntegers
    List<LocalDate> listOfDates
    List<SnapshotEntity> listOfEntities
    List<DummyAddress> listOfValueObjects

    Set<Integer> setOfIntegers
    Set<LocalDate> setOfDates
    Set<SnapshotEntity> setOfEntities
    Set<DummyAddress> setOfValueObjects

    Optional<Integer> optionalInteger
    Optional<LocalDate> optionalDate
    Optional<SnapshotEntity> optionalEntity
    Optional<DummyAddress> optionalValueObject

    Map<String, Integer> mapOfPrimitives
    Map<LocalDate,BigDecimal> mapOfValues
    Map<String,DummyAddress> mapPrimitiveToVO
    Map<String,SnapshotEntity> mapPrimitiveToEntity
    Map<SnapshotEntity, SnapshotEntity> mapOfEntities
    Map<Object, Object> polymorficMap
    Map<String,EnumSet<DummyEnum>> mapOfGenericValues

    ShallowPhone shallowPhone

    Map<DummyAddress,String> mapVoToPrimitive           //not supported

    Map nonParametrizedMap                              //not supported

    @Id
    public int getId() {
        id
    }
}
