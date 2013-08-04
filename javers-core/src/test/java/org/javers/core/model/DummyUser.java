package org.javers.core.model;

/**
 * @author bartosz walacik
 */
public class DummyUser {

    private boolean flag;
    private Boolean bigFlag;
    private int age;
    private String name;
    private Integer largeInt;

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
}
