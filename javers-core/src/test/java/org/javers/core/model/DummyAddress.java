package org.javers.core.model;

/**
 * (Multi) Value Object
 *
 * @author bartosz walacik
 */
public class DummyAddress {
    public enum Kind {HOME, OFFICE}

    private String city;
    private String street;
    private Kind kind;

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
}
