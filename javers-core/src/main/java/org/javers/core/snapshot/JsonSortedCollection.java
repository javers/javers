package org.javers.core.snapshot;

import java.util.Collection;
import java.util.Collections;

public class JsonSortedCollection {
    private final Collection<?> sourceCollection;

    public JsonSortedCollection(Collection<?> sourceCollection) {
        this.sourceCollection = sourceCollection == null ? Collections.EMPTY_SET : sourceCollection;
    }

    public Collection<?> getSourceCollection() {
        return sourceCollection;
    }
}
