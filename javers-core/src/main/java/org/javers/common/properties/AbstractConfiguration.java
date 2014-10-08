package org.javers.common.properties;

/**
 * @author bartosz walacik
 */
public abstract class AbstractConfiguration {
    private PropertyConfiguration propertyConfiguration;

    public AbstractConfiguration(PropertyConfiguration propertyConfiguration) {
        this.propertyConfiguration = propertyConfiguration;
    }

    public void readProperties(String classpathName) {
        propertyConfiguration = new PropertyConfiguration(classpathName);
        assemble();
    }

    protected <T extends Enum<T>> T getEnumProperty(String propertyKey, Class<T> enumType) {
        if (!containsPropertyKey(propertyKey)) {
            return null;
        }
        return propertyConfiguration.getEnumProperty(propertyKey, enumType);
    }

    protected String getStringProperty(String propertyKey) {
        return propertyConfiguration.getStringProperty(propertyKey);
    }

    protected boolean getBooleanProperty(String propertyKey) {
        if (!containsPropertyKey(propertyKey)) {
            return false;
        }
        return propertyConfiguration.getBooleanProperty(propertyKey);
    }

    public boolean containsPropertyKey(String propertyKey) {
        return propertyConfiguration.contains(propertyKey);
    }

    /**
     * Called after reading properties file,
     * override to read configuration from it.
     * <br>
     * Should not override property to null if propertyKey is not found in file
     */
    protected abstract void assemble();
}
