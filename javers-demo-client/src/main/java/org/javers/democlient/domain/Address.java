package org.javers.democlient.domain;

/**
 * immutable Value Object
 *
 * @author bartosz walacik
 */
public class Address {
    private final String street;
    private final String city;
    private final String postCode;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
        this.postCode = null;
    }

    public Address(String street, String city, String postCode) {
        this.street = street;
        this.city = city;
        this.postCode = postCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostCode() {
        return postCode;
    }
}
