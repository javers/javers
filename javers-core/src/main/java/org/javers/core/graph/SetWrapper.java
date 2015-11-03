package org.javers.core.graph;

import java.util.Set;

public class SetWrapper implements CollectionWrapper {

    private final Set<Object> set;

    public SetWrapper(Set set) {
        this.set = set;
    }

    public Set<Object> getSet() {
        return set;
    }
}
