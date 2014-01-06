package org.javers.core.configuration;

import org.javers.core.configuration.PropertyConfiguration;

/**
 * @author bartosz walacik
 */
public abstract class AbstractConfiguration {
    protected PropertyConfiguration propertyConfiguration;

    public void readProperties(String classpathName) {
        propertyConfiguration = new PropertyConfiguration(classpathName);
        assemble();
    }

    protected <T extends Enum<T>> T getEnumProperty(String propertyKey, Class<T> enumType) {
        return propertyConfiguration.getEnumProperty(propertyKey, enumType);
    }

    protected String getStringProperty(String propertyKey) {
        return propertyConfiguration.getStringProperty(propertyKey);
    }

    public boolean containsPropertyKey(String propertyKey) {
        return propertyConfiguration.contains(propertyKey);
    }

    /**
     * Called after reading properties file,
     * override to read configuration from it.
     * <br/>
     * Should not override property to null if propertyKey is not found in file
     */
    protected abstract void assemble();
}
