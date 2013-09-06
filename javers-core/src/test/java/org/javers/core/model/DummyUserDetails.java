package org.javers.core.model;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class DummyUserDetails {

    private Long id;
    private String someValue;
    private boolean isTrue;
    private DummyAddress dummyAddress;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSomeValue() {
        return someValue;
    }

    public void setSomeValue(String someValue) {
        this.someValue = someValue;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }

    public DummyAddress getDummyAddress() {
        return dummyAddress;
    }

    public void setDummyAddress(DummyAddress dummyAddress) {
        this.dummyAddress = dummyAddress;
    }
}
