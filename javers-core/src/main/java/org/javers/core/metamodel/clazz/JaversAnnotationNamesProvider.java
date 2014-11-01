package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Sets;
import org.javers.core.metamodel.annotation.Value;
import org.javers.core.metamodel.annotation.ValueObject;
import org.javers.core.metamodel.annotation.Entity;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public class JaversAnnotationNamesProvider implements AnnotationNamesProvider {

    @Override
    public Set<String> getEntityAlias() {
        return Sets.asSet(Entity.class.getSimpleName());
    }

    @Override
    public Set<String> getValueObjectAlias() {
        return Sets.asSet(ValueObject.class.getSimpleName());
    }

    @Override
    public Set<String> getValueAlias() {
        return Sets.asSet(Value.class.getSimpleName());
    }
}
