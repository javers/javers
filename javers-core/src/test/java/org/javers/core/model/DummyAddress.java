package org.javers.core.model;

import javax.persistence.Transient;

/**
 * (Multi) Value Object
 *
 * @author bartosz walacik
 */
public class DummyAddress extends AbstractDummyAddress{
    public enum Kind {HOME, OFFICE}

    private String city;
    private String street;
    private Kind kind;
    private DummyNetworkAddress networkAddress;
    private transient int someTransientField;

    public DummyAddress() {
    }

    public DummyAddress(String city, String street) {
        this.city = city;
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public DummyNetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(DummyNetworkAddress networkAddress) {
        this.networkAddress = networkAddress;
    }

    @Transient
    public int getSomeTransientField() {
        return someTransientField;
    }

    public void setSomeTransientField(int someTransientField) {
        this.someTransientField = someTransientField;
    }
}
