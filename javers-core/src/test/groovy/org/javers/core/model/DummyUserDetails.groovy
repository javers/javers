package org.javers.core.model

import javax.persistence.Id

/**
 * @author bartosz walacik
 */
class DummyUserDetails {
    static int DEFAULT_ID = 1

    @Id
    Long id
    String someValue
    boolean isTrue
    DummyAddress dummyAddress
    List<DummyAddress> addressList = []
    List<Integer> integerList = []

    @Id
    public Long getId() {
        id
    }

    static DummyUserDetails dummyUserDetails(int id) {
        new DummyUserDetails(id:id)
    }

    static DummyUserDetails dummyUserDetails() {
        dummyUserDetails(DEFAULT_ID)
    }

    DummyUserDetails withAddress(String street, String city) {
        this.dummyAddress = new DummyAddress(street, city)
        this
    }

    DummyUserDetails withAddress() {
        this.dummyAddress = new DummyAddress("street", "city")
        this
    }

    DummyUserDetails withAddresses(DummyAddress... dummyAddress) {
        this.addressList.addAll(dummyAddress)
        this
    }
}
