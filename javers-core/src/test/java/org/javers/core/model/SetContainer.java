package org.javers.core.model;

import java.util.Set;

public class SetContainer {
    Set<DummyUser> dummyUsers;

    public SetContainer(Set<DummyUser> dummyUsers) {
        this.dummyUsers = dummyUsers;
    }

    public Set<DummyUser> getDummyUsers() {
        return dummyUsers;
    }
}
