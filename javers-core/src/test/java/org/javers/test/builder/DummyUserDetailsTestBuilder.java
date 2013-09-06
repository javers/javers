package org.javers.test.builder;

import org.javers.core.model.DummyUserDetails;

/**
 * @author bartosz walacik
 */
public class DummyUserDetailsTestBuilder {
    private static final long DEFAULT_ID = 1L;
    private DummyUserDetails dummyUserDetails;

    private DummyUserDetailsTestBuilder() {
        dummyUserDetails = new DummyUserDetails();
    }

    public static DummyUserDetailsTestBuilder dummyUserDetails() {
        return new DummyUserDetailsTestBuilder();
    }

    public DummyUserDetails build() {
        if(dummyUserDetails.getId() == null) {
            dummyUserDetails.setId(DEFAULT_ID);
        }
        return dummyUserDetails;
    }

    public DummyUserDetailsTestBuilder withId(long id) {
        dummyUserDetails.setId(id);
        return this;
    }
}
