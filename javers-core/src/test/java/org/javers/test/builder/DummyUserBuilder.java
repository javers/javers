package org.javers.test.builder;

import org.javers.core.model.DummyUser;

import static org.javers.test.builder.DummyUserDetailsTestBuilder.dummyUserDetails;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class DummyUserBuilder {

    private DummyUser dummyUser;

    private DummyUserBuilder() {
        dummyUser = new DummyUser();
    }

    public static DummyUserBuilder dummyUser() {
        return new DummyUserBuilder();
    }

    public DummyUser build() {
        return dummyUser;
    }

    public DummyUserBuilder withName(String name) {
        dummyUser.setName(name);
        return this;
    }

    public DummyUserBuilder withSupervisor(String supervisorName) {
        dummyUser.setSupervisor(new DummyUser(supervisorName));
        return this;
    }

    public DummyUserBuilder withSupervisor(DummyUser supervisor) {
        dummyUser.setSupervisor(supervisor);
        return this;
    }


    public DummyUserBuilder withDetails() {
        dummyUser.setDummyUserDetails(dummyUserDetails().build());
        return this;
    }

    public DummyUserBuilder withDetails(long id) {
        dummyUser.setDummyUserDetails(dummyUserDetails().withId(id).build());
        return this;
    }

}
