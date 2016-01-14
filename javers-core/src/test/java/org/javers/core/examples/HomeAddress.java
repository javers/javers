package org.javers.core.examples;

class HomeAddress extends Address {
    private String city;
    private String street;

    HomeAddress(String city, String street, boolean verified) {
        super(verified);
        this.city = city;
        this.street = street;
    }
}
