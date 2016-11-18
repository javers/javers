package org.javers.core.metamodel.scanner;

/**
 * Managed class property propertyScanner
 *
 * @author pawel szymczyk
 */
abstract class PropertyScanner {
    abstract public PropertyScan scan(Class<?> managedClass, boolean ignoreDeclaredProperties);

    public PropertyScan scan(Class<?> managedClass) {
        return scan(managedClass, false);
    }
}
