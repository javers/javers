package org.javers.test.builder;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyUserDetails;

/**
 * @author bartosz walacik
 */
public class DummyUserDetailsBuilder {
    private static final long DEFAULT_ID = 1L;
    private DummyUserDetails dummyUserDetails;

    private DummyUserDetailsBuilder() {
        dummyUserDetails = new DummyUserDetails();
    }

    public static DummyUserDetailsBuilder dummyUserDetails(long withId) {
        return new DummyUserDetailsBuilder().withId(withId);
    }

    public static DummyUserDetailsBuilder dummyUserDetails() {
        return new DummyUserDetailsBuilder();
    }


    public DummyUserDetails build() {
        if(dummyUserDetails.getId() == null) {
            dummyUserDetails.setId(DEFAULT_ID);
        }
        return dummyUserDetails;
    }

    public DummyUserDetailsBuilder withId(long id) {
        dummyUserDetails.setId(id);
        return this;
    }

    public DummyUserDetailsBuilder withAddress(String street, String city) {
        dummyUserDetails.setDummyAddress(new DummyAddress(street, city));
        return this;
    }

    public DummyUserDetailsBuilder withAddress() {
        dummyUserDetails.setDummyAddress(new DummyAddress("street", "city"));
        return this;
    }
}
