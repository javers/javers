package org.javers.core.model

import com.google.common.collect.Multimap
import com.google.common.collect.Multiset
import groovy.transform.EqualsAndHashCode

import java.time.LocalDate

import javax.persistence.Id

/**
 * @author bartosz walacik
 */
@EqualsAndHashCode
class SnapshotEntity {

    @Id
    int id = 1

    enum DummyEnum { val1, val2, val3 }

    LocalDate dob

    int intProperty

    private SnapshotEntity entityRef

    private DummyAddress valueObjectRef

    Integer[] arrayOfIntegers
    int[] arrayOfInts
    LocalDate[] arrayOfDates
    SnapshotEntity[] arrayOfEntities
    DummyAddress[] arrayOfValueObjects

    List<Integer> listOfIntegers
    List<LocalDate> listOfDates
    List<SnapshotEntity> listOfEntities
    List<DummyAddress> listOfValueObjects
    List<Object> polymorficList

    Set<Integer> setOfIntegers
    Set<LocalDate> setOfDates
    Set<SnapshotEntity> setOfEntities
    Set<DummyAddress> setOfValueObjects

    Optional<Integer> optionalInteger
    Optional<LocalDate> optionalDate
    Optional<SnapshotEntity> optionalEntity
    Optional<DummyAddress> optionalValueObject

    Multiset<String> multiSetOfPrimitives
    Multiset<DummyAddress> multiSetValueObject
    Multiset<SnapshotEntity> multiSetOfEntities

    Multimap<String,String> multiMapOfPrimitives
    Multimap<String, DummyAddress> multimapPrimitiveToValueObject
    Multimap<String, SnapshotEntity> multiMapPrimitiveToEntity
    Multimap<SnapshotEntity, SnapshotEntity> multiMapEntityToEntity
    Multimap<DummyAddress, DummyAddress> multimapValueObjectToValueObject //not suppored

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
    int getId() {
        id
    }

    LocalDate getDob() {
        return dob
    }

    int getIntProperty() {
        return intProperty
    }

    SnapshotEntity getEntityRef() {
        return entityRef
    }

    DummyAddress getValueObjectRef() {
        return valueObjectRef
    }

    Integer[] getArrayOfIntegers() {
        return arrayOfIntegers
    }

    int[] getArrayOfInts() {
        return arrayOfInts
    }

    LocalDate[] getArrayOfDates() {
        return arrayOfDates
    }

    SnapshotEntity[] getArrayOfEntities() {
        return arrayOfEntities
    }

    DummyAddress[] getArrayOfValueObjects() {
        return arrayOfValueObjects
    }

    List<Integer> getListOfIntegers() {
        return listOfIntegers
    }

    List<LocalDate> getListOfDates() {
        return listOfDates
    }

    List<SnapshotEntity> getListOfEntities() {
        return listOfEntities
    }

    List<DummyAddress> getListOfValueObjects() {
        return listOfValueObjects
    }

    List<Object> getPolymorficList() {
        return polymorficList
    }

    Set<Integer> getSetOfIntegers() {
        return setOfIntegers
    }

    Set<LocalDate> getSetOfDates() {
        return setOfDates
    }

    Set<SnapshotEntity> getSetOfEntities() {
        return setOfEntities
    }

    Set<DummyAddress> getSetOfValueObjects() {
        return setOfValueObjects
    }

    Optional<Integer> getOptionalInteger() {
        return optionalInteger
    }

    Optional<LocalDate> getOptionalDate() {
        return optionalDate
    }

    Optional<SnapshotEntity> getOptionalEntity() {
        return optionalEntity
    }

    Optional<DummyAddress> getOptionalValueObject() {
        return optionalValueObject
    }

    Multiset<String> getMultiSetOfPrimitives() {
        return multiSetOfPrimitives
    }

    Multiset<DummyAddress> getMultiSetValueObject() {
        return multiSetValueObject
    }

    Multiset<SnapshotEntity> getMultiSetOfEntities() {
        return multiSetOfEntities
    }

    Multimap<String, String> getMultiMapOfPrimitives() {
        return multiMapOfPrimitives
    }

    Multimap<String, DummyAddress> getMultimapPrimitiveToValueObject() {
        return multimapPrimitiveToValueObject
    }

    Multimap<String, SnapshotEntity> getMultiMapPrimitiveToEntity() {
        return multiMapPrimitiveToEntity
    }

    Multimap<SnapshotEntity, SnapshotEntity> getMultiMapEntityToEntity() {
        return multiMapEntityToEntity
    }

    Multimap<DummyAddress, DummyAddress> getMultimapValueObjectToValueObject() {
        return multimapValueObjectToValueObject
    }

    Map<String, Integer> getMapOfPrimitives() {
        return mapOfPrimitives
    }

    Map<LocalDate, BigDecimal> getMapOfValues() {
        return mapOfValues
    }

    Map<String, DummyAddress> getMapPrimitiveToVO() {
        return mapPrimitiveToVO
    }

    Map<String, SnapshotEntity> getMapPrimitiveToEntity() {
        return mapPrimitiveToEntity
    }

    Map<SnapshotEntity, SnapshotEntity> getMapOfEntities() {
        return mapOfEntities
    }

    Map<Object, Object> getPolymorficMap() {
        return polymorficMap
    }

    Map<String, EnumSet<DummyEnum>> getMapOfGenericValues() {
        return mapOfGenericValues
    }

    ShallowPhone getShallowPhone() {
        return shallowPhone
    }

    Map<DummyAddress, String> getMapVoToPrimitive() {
        return mapVoToPrimitive
    }

    Map getNonParametrizedMap() {
        return nonParametrizedMap
    }
}
