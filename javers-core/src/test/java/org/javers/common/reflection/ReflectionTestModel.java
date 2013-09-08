package org.javers.common.reflection;

import org.javers.core.model.DummyUser;

import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ReflectionTestModel {

    private List<DummyUser> dummyUserList;
    private Set<DummyUser> dummyUserSet;
    private Queue<DummyUser> dummyUserQueue;

    public List<DummyUser> getDummyUserList() {
        return dummyUserList;
    }

    public Set<DummyUser> getDummyUserSet() {
        return dummyUserSet;
    }

    public Queue<DummyUser> getDummyUserQueue() {
        return dummyUserQueue;
    }
}
