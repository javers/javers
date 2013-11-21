package org.javers.core.model;

import com.google.common.collect.ImmutableList;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class DummyUser extends AbstractDummyUser {

    public enum Sex {FEMALE, MALE, OCCASIONALLY}

    private transient int someTransientField;

    //primitives and primitive boxes
    private boolean flag;
    private Boolean bigFlag;
    private int age;

    //enum
    private Sex sex;

    @Id
    private String name;
    private Integer largeInt;

    //collections
    private Set<String> stringSet;
    private List<Integer> integerList;

    //arrays
    private int[] intArray;

     //reference
    private DummyUser supervisor;
    private DummyUserDetails dummyUserDetails;
    private List<DummyUserDetails> dummyUserDetailsList;
    private List<DummyUser> employeesList;

    public DummyUser() {
    }

    public DummyUser(String name) {
        this.name = name;
    }

    public void addEmployee(DummyUser dummyUser) {
        if (employeesList == null) {
            employeesList = new ArrayList<>();
        }
        employeesList.add(dummyUser);
    }

    @Transient
    public int getSomeTransientField() {
        return someTransientField;
    }

    public DummyUser getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(DummyUser supervisor) {
        this.supervisor = supervisor;
    }

    public Boolean getBigFlag() {
        return bigFlag;
    }

    public void setBigFlag(Boolean bigFlag) {
        this.bigFlag = bigFlag;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Id
    public String getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getLargeInt() {
        return largeInt;
    }

    public void setLargeInt(Integer largeInt) {
        this.largeInt = largeInt;
    }

    public Set<String> getStringSet() {
        return stringSet;
    }

    public void setStringSet(Set<String> stringSet) {
        this.stringSet = stringSet;
    }

    public List<Integer> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<Integer> integerList) {
        this.integerList = integerList;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public void setSomeTransientField(int someTransientField) {
        this.someTransientField = someTransientField;
    }

    public DummyUserDetails getDummyUserDetails() {
        return dummyUserDetails;
    }

    public void setDummyUserDetails(DummyUserDetails dummyUserDetails) {
        this.dummyUserDetails = dummyUserDetails;
    }

    public List<DummyUserDetails> getDummyUserDetailsList() {
        return dummyUserDetailsList;
    }

    public void setDummyUserDetailsList(List<DummyUserDetails> dummyUserDetailsList) {
        this.dummyUserDetailsList = dummyUserDetailsList;
    }

    public List<DummyUser> getEmployeesList() {
        return employeesList;
    }

    public void setEmployeesList(DummyUser... employeesList) {
        this.employeesList = Arrays.asList(employeesList);
    }

    public void setEmployeesList(List<DummyUser> employeesList) {
        this.employeesList = employeesList;
    }
}
