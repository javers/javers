package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.domain.Diff;

/**
 * @author Maciej Zasada
 */
public class DiffAssert extends AbstractAssert<DiffAssert, Diff> {

    private DiffAssert(Diff actual) {
        super(actual, DiffAssert.class);
    }

    public static DiffAssert assertThat(Diff actual) {
        return new DiffAssert(actual);
    }

    public DiffAssert hasChangesCount(int count) {
        Assertions.assertThat(actual.getChanges()).hasSize(count);
        return this;
    }

    public ChangeAssert getChangeAtIndex(int index) {
        Assertions.assertThat(actual.getChanges().size() > index);
        return ChangeAssert.assertThat(actual.getChanges().get(index));
    }
}
