package org.javers.core.metamodel.property;

/**
 * Managed class property propertyScanner
 *
 * @author pawel szymczyk
 */
public interface PropertyScanner {
    PropertyScan scan(Class<?> managedClass);
}
