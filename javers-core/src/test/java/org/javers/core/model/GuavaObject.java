package org.javers.core.model;

import com.google.common.collect.Multimap;

/**
 * @author bartosz walacik
 */
public class GuavaObject {
    private Multimap multimap;

    public Multimap getMultimap() {
        return multimap;
    }

    public void setMultimap(Multimap multimap) {
        this.multimap = multimap;
    }
}
