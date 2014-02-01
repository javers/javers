package org.javers.test.builder

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserDetails

/**
 * @author bartosz walacik
 */
class DummyUserDetailsBuilder {
    private static final def DEFAULT_ID = 1
    private DummyUserDetails dummyUserDetails

    private DummyUserDetailsBuilder() {
        dummyUserDetails = new DummyUserDetails()
    }

    static DummyUserDetailsBuilder dummyUserDetails(long withId) {
        new DummyUserDetailsBuilder().withId(withId)
    }

    static DummyUserDetailsBuilder dummyUserDetails() {
        new DummyUserDetailsBuilder()
    }

    DummyUserDetails build() {
        if (dummyUserDetails.id == null) {
            dummyUserDetails.id = DEFAULT_ID
        }
        dummyUserDetails
    }

    DummyUserDetailsBuilder withId(long id) {
        dummyUserDetails.id = id
        this
    }

    DummyUserDetailsBuilder withAddress(String street, String city) {
        dummyUserDetails.dummyAddress = new DummyAddress(street, city)
        this
    }

    DummyUserDetailsBuilder withAddress() {
        dummyUserDetails.dummyAddress = new DummyAddress("street", "city")
        this
    }
}
