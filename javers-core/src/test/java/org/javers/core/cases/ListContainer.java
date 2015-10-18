package org.javers.core.cases;

import org.javers.core.model.DummyUser;

import java.util.List;

public class ListContainer {
    List<DummyUser> dummyUsers;

    public ListContainer(List<DummyUser> dummyUsers) {
        this.dummyUsers = dummyUsers;
    }
}
