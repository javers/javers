package org.javers.core.examples.model;


import org.javers.common.string.ToStringBuilder;

/**
 * @author bartosz walacik
 */
public class Address {
    private String city;

    private String street;

    public Address() {
    }

    public Address(String city) {
        this.city = city;
    }

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "city", city,
                "street", street
        );
    }
}
