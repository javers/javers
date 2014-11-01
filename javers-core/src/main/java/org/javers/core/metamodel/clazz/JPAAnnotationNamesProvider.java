package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Sets;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public class JPAAnnotationNamesProvider implements AnnotationNamesProvider {
    @Override
    public Set<String> getEntityAlias() {
        return Sets.asSet("Entity", "MappedSuperclass");
    }

    @Override
    public Set<String> getValueObjectAlias() {
        return Sets.asSet("Embeddable");
    }

    @Override
    public Set<String> getValueAlias() {
        return Sets.asSet("Value");
    }
}
