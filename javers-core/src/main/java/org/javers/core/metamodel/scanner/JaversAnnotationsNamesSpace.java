package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Sets;
import org.javers.core.metamodel.annotation.*;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class JaversAnnotationsNamesSpace implements AnnotationsNameSpace {
    public static final Class<? extends Annotation> ENTITY_ANN = Entity.class;
    public static final Class<? extends Annotation> VALUE_OBJECT_ANN = ValueObject.class;
    public static final Class<? extends Annotation> VALUE_ANN = Value.class;
    public static final Class<? extends Annotation> DIFF_IGNORE_ANN = DiffIgnore.class;
    public static final Class<? extends Annotation> SHALLOW_REFERENCE_ANN = ShallowReference.class;
    public static final Class<? extends Annotation> TYPE_NAME_ANN = TypeName.class;
    public static final Class<? extends Annotation> PROPERTY_NAME_ANN = PropertyName.class;

    @Override
    public Set<String> getEntityAliases() {
        return Sets.asSet(ENTITY_ANN.getSimpleName());
    }

    @Override
    public Set<String> getValueObjectAliases() {
        return Sets.asSet(VALUE_OBJECT_ANN.getSimpleName());
    }

    @Override
    public Set<String> getValueAliases() {
        return Sets.asSet(VALUE_ANN.getSimpleName());
    }

    @Override
    public Set<String> getTransientPropertyAliases() {
        return Sets.asSet(DIFF_IGNORE_ANN.getSimpleName());
    }

    @Override
    public Set<String> getShallowReferenceAliases() {
        return Sets.asSet(SHALLOW_REFERENCE_ANN.getSimpleName());
    }

    @Override
    public Set<String> getTypeNameAliases() {
        return Sets.asSet(TYPE_NAME_ANN.getSimpleName());
    }

    @Override
    public Set<String> getIgnoredTypeAliases() {
        return Sets.asSet(DIFF_IGNORE_ANN.getSimpleName());
    }

    @Override
    public Set<String> getPropertyNameAliases() {
        return Sets.asSet(PROPERTY_NAME_ANN.getSimpleName());
    }
}
