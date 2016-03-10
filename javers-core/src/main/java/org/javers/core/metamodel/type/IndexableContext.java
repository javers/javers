package org.javers.core.metamodel.type;

import org.javers.core.metamodel.object.EnumeratorContext;

/**
 * Context for indexable properties: Lists & Arrays
 *
 * @author bartosz walacik
 */
class IndexableContext implements EnumeratorContext {
    private int index;

    @Override
    public String getPath() {
        return ""+(index++);
    }
}
