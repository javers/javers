package org.javers.core.model;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class DummyUserDetails {

    @Id
    private Long id;
    private String someValue;
    private boolean isTrue;
    private DummyAddress dummyAddress;
    private List<DummyAddress> addressList = new ArrayList<>();

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

    public List<DummyAddress> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<DummyAddress> addressList) {
        this.addressList = addressList;
    }
}
