package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversMember;
import org.javers.common.reflection.ReflectionUtil;

import java.util.List;

/**
 * @author pawel szymczyk
 */
class FieldBasedPropertyScanner extends PropertyScanner {

    FieldBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        super(annotationNamesProvider);
    }

    @Override
    List<JaversMember> getMembers(Class<?> managedClass) {
        return (List)ReflectionUtil.getAllPersistentFields(managedClass);
    }
}
