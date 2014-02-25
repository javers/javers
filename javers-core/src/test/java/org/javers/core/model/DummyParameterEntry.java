package org.javers.core.model;

import java.util.Map;

/**
 * @author bartosz walacik
 */
public class DummyParameterEntry {
    private Map<String, Object> levels;

    public DummyParameterEntry(Map<String, Object> levels) {
        this.levels = levels;
    }

    public Map<String, Object> getLevels() {
        return levels;
    }
}
