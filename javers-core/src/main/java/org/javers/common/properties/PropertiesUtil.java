package org.javers.common.properties;

import org.javers.common.validation.Validate;
import org.javers.common.exception.JaversException;

import static org.javers.common.exception.JaversExceptionCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author bartosz walacik
 */
public class PropertiesUtil {

    /**
     * @throws JaversException UNDEFINED_PROPERTY
     */
    public static String getStringProperty(Properties properties, String propertyKey) {
        Validate.argumentIsNotNull(properties);
        Validate.argumentIsNotNull(propertyKey);

        if (!properties.containsKey(propertyKey)) {
            throw new JaversException(UNDEFINED_PROPERTY,propertyKey);
        }
        return properties.getProperty(propertyKey);
    }

    /**
     * @throws JaversException UNDEFINED_PROPERTY
     */
    public static boolean getBooleanProperty(Properties properties, String propertyKey) {
        String val = getStringProperty(properties, propertyKey);

        return Boolean.parseBoolean(val);
    }


    /**
     * @throws JaversException UNDEFINED_PROPERTY
     * @throws JaversException MALFORMED_PROPERTY
     */
    public static <T extends Enum<T>> T getEnumProperty(Properties properties, String propertyKey, Class<T> enumType) {
        String enumName = getStringProperty(properties,propertyKey);
        Validate.argumentIsNotNull(enumType);

        try {
            return Enum.valueOf(enumType, enumName);
        } catch (IllegalArgumentException e) {
            throw new JaversException(MALFORMED_PROPERTY, enumName, propertyKey);
        }
    }

    /**
     * @see #loadProperties(String, java.util.Properties)
     */
    public static Properties getProperties(String classpathName) {
        Properties properties = new Properties();
        loadProperties(classpathName,properties);
        return properties;
    }

    /**
     * loads a properties file from classpath using default classloader
     *
     * @param classpathName classpath resource name
     * @throws JaversException CLASSPATH_RESOURCE_NOT_FOUND
     * @see ClassLoader#getResourceAsStream(String)
     */
    public static void loadProperties(String classpathName, Properties toProps) {
        Validate.argumentIsNotNull(classpathName);
        Validate.argumentIsNotNull(toProps);

        InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(classpathName);

        if (inputStream == null) {
            throw new JaversException(CLASSPATH_RESOURCE_NOT_FOUND, classpathName);
        }

        try {
            toProps.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
