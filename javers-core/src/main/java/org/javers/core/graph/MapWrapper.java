package org.javers.core.graph;

import java.util.Map;

public class MapWrapper {

    private final Map<Object, Object> map;

    public MapWrapper(Map map) {
        this.map = map;
    }
}
