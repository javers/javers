package org.javers.core;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 *
 * @author bartosz walacik
 */
public class JaversFactory {

    private Javers javers;

    public JaversFactory() {
        javers = new Javers();
    }

    public Javers build() {
        return javers;
    }

    public JaversFactory manageClasses(Class<?>... managedClasses) {
        for (Class<?> managedClass : managedClasses) {
            javers.manage(managedClass);
        }
        return this;
    }
}
