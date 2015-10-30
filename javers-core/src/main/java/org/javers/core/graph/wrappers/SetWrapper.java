package org.javers.core.graph.wrappers;

import java.util.Set;

public class SetWrapper implements SpecifiedClassCollectionWrapper {

    private final Set<Object> set;

    public SetWrapper(Set set) {
        this.set = set;
    }

    @Override
    public String getWrappedCollectionFieldName() {
        return "set";
    }
}
