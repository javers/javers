package org.javers.core;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 *
 * @author bartosz walacik
 */
public class JaversFactory {

    private Javers javers;

    private JaversFactory() {
        javers = new Javers();
    }

    public static JaversFactory javers() {
        return new JaversFactory();
    }

    public Javers build() {
        return javers;
    }

    public JaversFactory managingClasses(Class<?>... managedClasses) {
        for (Class<?> managedClass : managedClasses) {
            javers.manage(managedClass);
        }
        return this;
    }
}
