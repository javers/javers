package org.javers.core.metamodel.property;

import java.util.List;

/**
 * Managed class property propertyScanner
 *
 * @author pawel szymczyk
 */
public interface PropertyScanner {
    List<Property> scan(Class<?> managedClass);
    Property scanSingleProperty(Class<?> managedClass, String propertyName);
}
