package org.javers.common.properties;

import java.util.Properties;
import org.javers.common.exception.JaversException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bartosz walacik
 */
public class PropertyConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PropertyConfiguration.class);

    /**
     * raw String properties bag, loaded from configuration file
     */
    private final Properties properties;

    /**
     * loads a properties file from classpath
     * @param classpathName classpath resource name, ex. "resources/config.properties"
     */
    public PropertyConfiguration(String classpathName) {
        logger.debug("reading properties file - "+classpathName);
        properties = PropertiesUtil.getProperties(classpathName);
    }

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

    /**
     * @throws JaversException UNDEFINED_PROPERTY
     */
    public boolean getBooleanProperty(String propertyKey) {
        return PropertiesUtil.getBooleanProperty(properties, propertyKey);
    }

}
