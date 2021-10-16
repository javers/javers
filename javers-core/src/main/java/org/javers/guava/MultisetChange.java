package org.javers.guava;

import com.google.common.collect.Multiset;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;

import java.util.List;

public class MultisetChange extends CollectionChange<Multiset<?>> {
    public MultisetChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Multiset left, Multiset right) {
        super(metadata, changes, left, right);
    }
}
