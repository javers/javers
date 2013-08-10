package org.javers.core.model;

import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class DummyUser {

    //primitives and primitive boxes
    private boolean flag;
    private Boolean bigFlag;
    private int age;
    private String name;
    private Integer largeInt;

    //containers
    private Set<String> stringSet;
    private List<Integer> integerList;
    private int[] intArray;

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

    public String getName() {
        return name;
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
}
