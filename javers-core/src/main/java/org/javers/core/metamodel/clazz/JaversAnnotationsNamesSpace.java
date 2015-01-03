package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Sets;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Value;
import org.javers.core.metamodel.annotation.ValueObject;
import org.javers.core.metamodel.annotation.Entity;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public class JaversAnnotationsNamesSpace implements AnnotationsNameSpace {

    @Override
    public Set<String> getEntityAliases() {
        return Sets.asSet(Entity.class.getSimpleName());
    }

    @Override
    public Set<String> getValueObjectAliases() {
        return Sets.asSet(ValueObject.class.getSimpleName());
    }

    @Override
    public Set<String> getValueAliases() {
        return Sets.asSet(Value.class.getSimpleName());
    }

    @Override
    public Set<String> getTransientPropertyAliases() {
        return Sets.asSet(DiffIgnore.class.getSimpleName());
    }
}
