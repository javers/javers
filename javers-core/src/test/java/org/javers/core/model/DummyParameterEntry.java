package org.javers.core.model;

import java.util.Map;

/**
 * @author bartosz walacik
 */
public class DummyParameterEntry {
    private Map<Object, Object> levels;

    public DummyParameterEntry(Map<Object, Object> levels) {
        this.levels = levels;
    }

    public Map<Object, Object> getLevels() {
        return levels;
    }
}
