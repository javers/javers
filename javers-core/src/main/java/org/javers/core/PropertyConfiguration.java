package org.javers.core;

import java.util.Properties;
import org.javers.core.exceptions.JaversException;
import org.javers.core.pico.PropertiesUtil;

/**
 * @author bartosz walacik
 */
public class PropertyConfiguration {
    /**
     * raw String properties bag, loaded from configuration file
     */
    private Properties properties;

    /**
     * Empty Configuration
     */
    public PropertyConfiguration() {
        properties = new Properties();
    }

    /**
     * loads a properties file from classpath
     * @param classpathName classpath resource name
     */
    public PropertyConfiguration(String classpathName) {
        properties = PropertiesUtil.getProperties(classpathName);
    }

    /**
     * assembles modeled properties from {@link #properties} bag
     * @throws JaversException if required property is not found
     * @throws JaversException if property (like enum) cann't be assembled
     */
    //protected abstract void assemble();

    /**
     * assembles mandatory enum property from {@link #properties} bag
     * @throws JaversException UNDEFINED_PROPERTY
     * @throws JaversException MALFORMED_PROPERTY
     */
    public <T extends Enum<T>> T getEnumProperty(String propertyKey, Class<T> enumType) {
        return PropertiesUtil.getEnumProperty(properties, propertyKey, enumType);
    }

    public boolean contains(String propertyKey) {
        return properties.containsKey(propertyKey);
    }

    /**
     * gets mandatory String property from {@link #properties} bag
     * @throws JaversException UNDEFINED_PROPERTY
     */
    public String getStringProperty(String propertyKey) {
        return PropertiesUtil.getStringProperty(properties, propertyKey);
    }

}
