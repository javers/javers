package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.domain.Change;
import org.javers.model.domain.Diff;
import org.javers.model.domain.changeType.NewObject;
import org.javers.model.domain.changeType.ObjectRemoved;
import org.javers.model.domain.changeType.ReferenceAdded;

/**
 * @author Maciej Zasada
 */
public class ChangeAssert extends AbstractAssert<ChangeAssert, Change> {

    private ChangeAssert(Change actual) {
        super(actual, ChangeAssert.class);
    }

    public static ChangeAssert assertThat(Change actual) {
        return new ChangeAssert(actual);
    }

    public ChangeAssert isNewObject() {
        Assertions.assertThat(actual).isExactlyInstanceOf(NewObject.class);
        return this;
    }

    public ChangeAssert isObjectRemoved() {
        Assertions.assertThat(actual).isExactlyInstanceOf(ObjectRemoved.class);
        return this;
    }

    public ChangeAssert hasCdoId(Object cdoId) {
        Assertions.assertThat(actual.getGlobalCdoId().getCdoId()).isEqualTo(cdoId);
        return this;
    }

    public ChangeAssert isReferenceAdded() {
        Assertions.assertThat(actual).isExactlyInstanceOf(ReferenceAdded.class);
        return this;
    }

    public ChangeAssert hasEntityTypeOf(Class<?> entityClass) {
        Assertions.assertThat(actual.getGlobalCdoId().getEntity().getSourceClass()).isEqualTo(entityClass);
        return this;
    }

    public ChangeAssert hasParentEqualTo(Diff parent) {
        Assertions.assertThat(actual.getParent()).isEqualTo(parent);
        return this;
    }

}
