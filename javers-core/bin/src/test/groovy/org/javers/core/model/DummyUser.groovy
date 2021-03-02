package org.javers.core.model

import org.javers.core.metamodel.annotation.DiffIgnore
import java.time.LocalDateTime
import javax.persistence.Id
import javax.persistence.Transient

/**
 * @author bartosz walacik
 */
class DummyUser extends AbstractDummyUser {
    public enum Sex {FEMALE, MALE, OCCASIONALLY}

    transient int someTransientField

    @Transient
    int propertyWithTransientAnn

    @DiffIgnore
    int propertyWithDiffIgnoreAnn

    DummyIgnoredType propertyWithDiffIgnoredType
    IgnoredSubType propertyWithDiffIgnoredSubtype

    //primitives and primitive boxes
    boolean flag
    Boolean bigFlag
    int age
    char _char
    String surname

    //enum
    Sex sex

    @Id
    String name
    Integer largeInt

    //collections
    Set<String> stringSet
    List<String> stringList
    List<Integer> integerList
    Map<String, LocalDateTime> primitiveMap
    Map<String, LocalDateTime> valueMap
    Map<String, DummyUserDetails> objectMap //not supported

    //arrays
    int[] intArray
    LocalDateTime[] dateTimes

     //reference
    DummyUser supervisor
    DummyUserDetails dummyUserDetails
    List<DummyUserDetails> dummyUserDetailsList
    List<DummyUser> employeesList

    DummyUser() {
    }

    DummyUser(String name) {
        this.name = name
    }

    DummyUser(String name, String surname) {
        this(name)
        this.surname = surname
    }

    static DummyUser dummyUser(String name){
        new DummyUser(name:name)
    }

    static DummyUser dummyUser(){
        new DummyUser(name:'name')
    }

    def addEmployee(DummyUser employee) {
        if (employeesList == null) {
            employeesList = []
        }
        employeesList << employee
        employee.supervisor = this
    }

    @Id
    String getName() {
        name
    }

    boolean getFlag() {
        return flag
    }

    void setFlag(boolean flag) {
        this.flag = flag
    }

    @Transient
    int getPropertyWithTransientAnn() {
        propertyWithTransientAnn
    }


    @DiffIgnore
    int getPropertyWithDiffIgnoreAnn() {
        propertyWithDiffIgnoreAnn
    }

    DummyUser withDetails(int id){
        this.dummyUserDetails = new DummyUserDetails(id:id)
        this
    }

    DummyUser withDetails(){
        withDetails(1)
    }

    DummyUser withAddress(String city) {
        if (dummyUserDetails == null) {
            withDetails()
        }
        dummyUserDetails.dummyAddress = new DummyAddress(city)
        this
    }

    DummyUser withAddresses(DummyAddress... addresses) {
        dummyUserDetails.addressList = addresses.toList()
        this
    }

    DummyUser withSex(Sex sex) {
        this.sex = sex
        this
    }

    DummyUser withPrimitiveMap(Map map) {
        this.primitiveMap = map
        this
    }

    DummyUser withValueMap(Map map) {
        this.valueMap = map
        this
    }

    DummyUser withStringsSet(Set strings) {
        this.stringSet = strings
        this
    }

    DummyUser withIntegerList(List list) {
        this.integerList = list
        this
    }

    DummyUser withAge(int age) {
        this.age = age
        this
    }

    DummyUser withBoxedFlag(Boolean boxedFlag) {
        this.bigFlag = boxedFlag
        this
    }

    DummyUser withInteger(Integer largeInt) {
        this.largeInt = largeInt
        this
    }

    DummyUser withFlag(boolean flag) {
        this.flag = flag
        this
    }

    DummyUser withSupervisor(String supervisorName) {
        this.supervisor = new DummyUser(supervisorName)
        this
    }

    DummyUser withSupervisor(DummyUser supervisor) {
        this.supervisor = supervisor
        this
    }

    DummyUser withEmployees(int numberOfEmployees) {
        numberOfEmployees.times {
            this.addEmployee(new DummyUser("Em${it+1}"))
        }
        this
    }

    DummyUser withEmployees(List employees) {
        employees.forEach {
            this.addEmployee(it)
        }
        this
    }

    DummyUser withDetailsList(int numberOfDetailsInList) {
        this.dummyUserDetailsList = (1 .. numberOfDetailsInList).collect({
            new DummyUserDetails(id:it)
        })
        this
    }

    DummyUser withIntArray(List<Integer> ints){
        this.intArray = ints as int[]
        this
    }
}
