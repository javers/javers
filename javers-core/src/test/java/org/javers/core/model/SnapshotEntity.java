package org.javers.core.model;

import org.joda.time.LocalDate;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class SnapshotEntity {

    public enum DummyEnum { val1, val2, val3 }

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
    private Map<String,SnapshotEntity> mapPrimitiveToEntity;
    private Map<SnapshotEntity, SnapshotEntity> mapOfEntities;
    private Map<Object,Object> polymorficMap;
    private Map<String,EnumSet<DummyEnum>> mapOfGenericValues;

    @Id
    public int getId() {
        return id;
    }

    public Map<Object, Object> getPolymorficMap() {
        return polymorficMap;
    }

    public void setPolymorficMap(Map<Object, Object> polymorficMap) {
        this.polymorficMap = polymorficMap;
    }

    private Map<DummyAddress,String> mapVoToPrimitive;           //not supported

    private Map nonParametrizedMap;                              //not supported

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public int getIntProperty() {
        return intProperty;
    }

    public void setIntProperty(int intProperty) {
        this.intProperty = intProperty;
    }

    public SnapshotEntity getEntityRef() {
        return entityRef;
    }

    public void setEntityRef(SnapshotEntity entityRef) {
        this.entityRef = entityRef;
    }

    public DummyAddress getValueObjectRef() {
        return valueObjectRef;
    }

    public void setValueObjectRef(DummyAddress valueObjectRef) {
        this.valueObjectRef = valueObjectRef;
    }

    public Integer[] getArrayOfIntegers() {
        return arrayOfIntegers;
    }

    public void setArrayOfIntegers(Integer[] arrayOfIntegers) {
        this.arrayOfIntegers = arrayOfIntegers;
    }

    public LocalDate[] getArrayOfDates() {
        return arrayOfDates;
    }

    public void setArrayOfDates(LocalDate[] arrayOfDates) {
        this.arrayOfDates = arrayOfDates;
    }

    public SnapshotEntity[] getArrayOfEntities() {
        return arrayOfEntities;
    }

    public void setArrayOfEntities(SnapshotEntity[] arrayOfEntities) {
        this.arrayOfEntities = arrayOfEntities;
    }

    public DummyAddress[] getArrayOfValueObjects() {
        return arrayOfValueObjects;
    }

    public void setArrayOfValueObjects(DummyAddress[] arrayOfValueObjects) {
        this.arrayOfValueObjects = arrayOfValueObjects;
    }

    public List<Integer> getListOfIntegers() {
        return listOfIntegers;
    }

    public void setListOfIntegers(List<Integer> listOfIntegers) {
        this.listOfIntegers = listOfIntegers;
    }

    public List<LocalDate> getListOfDates() {
        return listOfDates;
    }

    public void setListOfDates(List<LocalDate> listOfDates) {
        this.listOfDates = listOfDates;
    }

    public List<SnapshotEntity> getListOfEntities() {
        return listOfEntities;
    }

    public void setListOfEntities(List<SnapshotEntity> listOfEntities) {
        this.listOfEntities = listOfEntities;
    }

    public List<DummyAddress> getListOfValueObjects() {
        return listOfValueObjects;
    }

    public void setListOfValueObjects(List<DummyAddress> listOfValueObjects) {
        this.listOfValueObjects = listOfValueObjects;
    }

    public Set<Integer> getSetOfIntegers() {
        return setOfIntegers;
    }

    public void setSetOfIntegers(Set<Integer> setOfIntegers) {
        this.setOfIntegers = setOfIntegers;
    }

    public Set<LocalDate> getSetOfDates() {
        return setOfDates;
    }

    public void setSetOfDates(Set<LocalDate> setOfDates) {
        this.setOfDates = setOfDates;
    }

    public Set<SnapshotEntity> getSetOfEntities() {
        return setOfEntities;
    }

    public void setSetOfEntities(Set<SnapshotEntity> setOfEntities) {
        this.setOfEntities = setOfEntities;
    }

    public Set<DummyAddress> getSetOfValueObjects() {
        return setOfValueObjects;
    }

    public void setSetOfValueObjects(Set<DummyAddress> setOfValueObjects) {
        this.setOfValueObjects = setOfValueObjects;
    }

    public Map<String, Integer> getMapOfPrimitives() {
        return mapOfPrimitives;
    }

    public void setMapOfPrimitives(Map<String, Integer> mapOfPrimitives) {
        this.mapOfPrimitives = mapOfPrimitives;
    }

    public Map<LocalDate, BigDecimal> getMapOfValues() {
        return mapOfValues;
    }

    public void setMapOfValues(Map<LocalDate, BigDecimal> mapOfValues) {
        this.mapOfValues = mapOfValues;
    }

    public Map<String, DummyAddress> getMapPrimitiveToVO() {
        return mapPrimitiveToVO;
    }

    public void setMapPrimitiveToVO(Map<String, DummyAddress> mapPrimitiveToVO) {
        this.mapPrimitiveToVO = mapPrimitiveToVO;
    }

    public Map<SnapshotEntity, SnapshotEntity> getMapOfEntities() {
        return mapOfEntities;
    }

    public void setMapOfEntities(Map<SnapshotEntity, SnapshotEntity> mapOfEntities) {
        this.mapOfEntities = mapOfEntities;
    }

    public Map<DummyAddress, String> getMapVoToPrimitive() {
        return mapVoToPrimitive;
    }

    public void setMapVoToPrimitive(Map<DummyAddress, String> mapVoToPrimitive) {
        this.mapVoToPrimitive = mapVoToPrimitive;
    }

    public Map getNonParametrizedMap() {
        return nonParametrizedMap;
    }

    public void setNonParametrizedMap(Map nonParametrizedMap) {
        this.nonParametrizedMap = nonParametrizedMap;
    }

    public Map<String, SnapshotEntity> getMapPrimitiveToEntity() {
        return mapPrimitiveToEntity;
    }

    public void setMapPrimitiveToEntity(Map<String, SnapshotEntity> mapPrimitiveToEntity) {
        this.mapPrimitiveToEntity = mapPrimitiveToEntity;
    }

    public Map<String,EnumSet<DummyEnum>> getMapOfGenericValues() {
        return mapOfGenericValues;
    }

    public void setMapOfGenericValues(Map<String,EnumSet<DummyEnum>> mapOfGenericValues) {
        this.mapOfGenericValues = mapOfGenericValues;
    }
}
