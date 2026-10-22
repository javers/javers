package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversMember;
import org.javers.common.reflection.ReflectionUtil;

import java.util.List;

/**
 * @author pawel szymczyk
 */
class BeanBasedPropertyScanner extends PropertyScanner {

    BeanBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider, final CascadeFallbackEvaluator cascadeFallbackEvaluator) {
        super(annotationNamesProvider, cascadeFallbackEvaluator);
    }


    @Override
    List<JaversMember> getMembers(Class<?> managedClass) {
        return (List)ReflectionUtil.getAllGetters(managedClass);
    }
}
