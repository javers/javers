package org.javers.core.model;

import java.util.List;

public class ListContainer {
    List<DummyUser> dummyUsers;

    public ListContainer(List<DummyUser> dummyUsers) {
        this.dummyUsers = dummyUsers;
    }

    public List<DummyUser> getDummyUsers() {
        return dummyUsers;
    }
}