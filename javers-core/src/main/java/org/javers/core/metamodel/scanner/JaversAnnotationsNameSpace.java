package org.javers.core.metamodel.scanner;

import org.javers.core.metamodel.annotation.*;
import java.lang.annotation.Annotation;

/**
 * @author bartosz walacik
 */
public class JaversAnnotationsNameSpace {
    public static final Class<? extends Annotation> ENTITY_ANN = Entity.class;
    public static final Class<? extends Annotation> VALUE_OBJECT_ANN = ValueObject.class;
    public static final Class<? extends Annotation> VALUE_ANN = Value.class;
    public static final Class<? extends Annotation> DIFF_IGNORE_ANN = DiffIgnore.class;
    public static final Class<? extends Annotation> SHALLOW_REFERENCE_ANN = ShallowReference.class;
    public static final Class<? extends Annotation> TYPE_NAME_ANN = TypeName.class;
    public static final Class<? extends Annotation> PROPERTY_NAME_ANN = PropertyName.class;
    public static final Class<? extends Annotation> IGNORE_DECLARED_PROPERTIES_ANN = IgnoreDeclaredProperties.class;
    public static final Class<? extends Annotation> DIFF_INCLUDE_ANN = DiffInclude.class;
}
