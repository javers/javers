package org.javers.core.metamodel.scanner;

/**
 * Facade for PropertyScanner and ClassAnnotationScanner
 * @author bartosz.walacik
 */
public class ClassScanner {

    private final PropertyScanner propertyScanner;
    private final ClassAnnotationsScanner classAnnotationsScanner;

    public ClassScanner(PropertyScanner propertyScanner, ClassAnnotationsScanner classAnnotationsScanner) {
        this.propertyScanner = propertyScanner;
        this.classAnnotationsScanner = classAnnotationsScanner;
    }

    public ClassScan scan(Class<?> managedClass){
        ClassAnnotationsScan classAnnScan = classAnnotationsScanner.scan(managedClass);
        return new ClassScan(propertyScanner.scan(managedClass, classAnnScan.hasIgnoreDeclaredProperties()), classAnnScan);
    }
}
