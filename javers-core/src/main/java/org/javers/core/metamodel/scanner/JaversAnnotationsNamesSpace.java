package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Sets;
import org.javers.core.metamodel.annotation.*;

import java.util.Set;

/**
 * @author bartosz walacik
 */
class JaversAnnotationsNamesSpace implements AnnotationsNameSpace {

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

    @Override
    public Set<String> getShallowReferenceAliases() {
        return Sets.asSet(ShallowReference.class.getSimpleName());
    }

    @Override
    public Set<String> getTypeNameAliases() {
        return Sets.asSet(TypeName.class.getSimpleName());
    }

    @Override
    public Set<String> getIgnoredTypeAliases() {
        return Sets.asSet(DiffIgnore.class.getSimpleName());
    }

    @Override
    public Set<String> getPropertyNameAliases() {
        return Sets.asSet(PropertyName.class.getSimpleName());
    }
}
