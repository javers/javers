package org.javers.core.diff;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.core.commit.CommitMetadata;

import java.util.*;

/**
 * @author bartosz walacik
 */
public class DiffBuilder {
    private final List<Change> changes = new ArrayList<>();
    private final PrettyValuePrinter valuePrinter;

    public DiffBuilder() {
        this(PrettyValuePrinter.getDefault());
    }

    public DiffBuilder(PrettyValuePrinter valuePrinter) {
        this.valuePrinter = valuePrinter;
    }

    public static Diff empty() {
        return new Diff(Collections.<Change>emptyList(), PrettyValuePrinter.getDefault());
    }

    public DiffBuilder addChange(Change change, Optional<Object> affectedCdo) {
        addChange(change);
        affectedCdo.ifPresent(change::setAffectedCdo);
        return this;
    }

    public DiffBuilder addChange(Change change) {
        changes.add(change);
        return this;
    }

    public DiffBuilder addChanges(Collection<Change> changeSet) {

        changeSet.forEach(change -> {
            addChange(change);
        });

        return this;
    }

    public Diff build() {
        return new Diff(changes, valuePrinter);
    }
}
