package org.javers.repository.jql;

import org.javers.common.collections.Sets;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.ManagedType;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ClassFilter extends Filter {
    private final Set<ManagedType> managedTypes;

    public ClassFilter(Set<ManagedType> managedTypes) {
        Validate.argumentIsNotNull(managedTypes);
        this.managedTypes =  managedTypes;
    }

    Set<ManagedType> getManagedTypes() {
        return managedTypes;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "types",
                Sets.transform(managedTypes, t -> t.getName()));
    }

    @Override
    boolean matches(GlobalId globalId) {
        return managedTypes.stream().anyMatch(id -> id.getName().equals(globalId.getTypeName()));
    }
}
