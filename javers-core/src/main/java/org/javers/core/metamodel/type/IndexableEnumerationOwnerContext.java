package org.javers.core.metamodel.type;

import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;

/**
 * @author bartosz.walacik
 */
class IndexableEnumerationOwnerContext extends EnumerationAwareOwnerContext {
    private int index;

    IndexableEnumerationOwnerContext(OwnerContext ownerContext) {
        super(ownerContext);
    }

    @Override
    public String getEnumeratorContextPath() {
        return ""+(index++);
    }
}
