package org.javers.core.metamodel.property;

import org.javers.common.reflection.JaversMethod;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.clazz.AnnotationNamesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class BeanBasedPropertyScanner implements PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    public BeanBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    @Override
    public List<Property> scan(Class<?> managedClass) {
        List<JaversMethod> getters = ReflectionUtil.findAllPersistentGetters(managedClass);
        List<Property> beanProperties = new ArrayList<>();

        for (JaversMethod getter : getters) {

            if (getter.hasAnyAnnotation(annotationNamesProvider.getTransientAliases())){
                continue;
            }

            Property beanProperty = new BeanProperty(getter);
            beanProperties.add(beanProperty);
        }
        return beanProperties;
    }
}
