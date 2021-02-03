package org.javers.core.model

import groovy.transform.ToString

import javax.persistence.Transient

/**
 * sample Value Object
 *
 * @author bartosz walacik
 */
@ToString
class DummyAddress extends AbstractDummyAddress{
    public enum Kind {HOME, OFFICE}

    String city
    String street
    Kind kind
    DummyNetworkAddress networkAddress
    transient int someTransientField
    static int staticInt
    List<String> moreCities

    DummyAddress() {
    }

    DummyAddress(String city) {
        this.city = city
    }

    DummyAddress(String city, String street) {
        this.city = city
        this.street = street
    }

    static int getStaticInt() {
        staticInt
    }

    static void setStaticInt(int staticInt) {
        DummyAddress.staticInt = staticInt;
    }

    @Transient
    int getSomeTransientField() {
        someTransientField
    }
}
