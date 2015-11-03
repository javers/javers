package org.javers.core.graph;

import java.util.List;

public class ListWrapper implements CollectionWrapper {

    private final List<Object> list;

    public ListWrapper(List list) {
        this.list = list;
    }

    public List<Object> getList() {
        return list;
    }
}
