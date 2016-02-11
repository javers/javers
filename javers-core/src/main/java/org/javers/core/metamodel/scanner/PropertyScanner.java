package org.javers.core.metamodel.scanner;

/**
 * Managed class property propertyScanner
 *
 * @author pawel szymczyk
 */
interface PropertyScanner {
    PropertyScan scan(Class<?> managedClass);
}
