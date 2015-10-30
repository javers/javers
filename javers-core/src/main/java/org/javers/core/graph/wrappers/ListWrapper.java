package org.javers.core.graph.wrappers;

import java.util.List;

public class ListWrapper implements SpecifiedClassCollectionWrapper {

    private final List<Object> list;

    public ListWrapper(List list) {
        this.list = list;
    }

    @Override
    public String getWrappedCollectionPropertyName() {
        return "list";
    }
}
