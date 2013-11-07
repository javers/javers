package org.javers.core;

import org.javers.common.validation.Validate;
import org.javers.core.pico.PropertiesUtil;

import java.util.Properties;

/**
 * @author bartosz walacik
 */
public class JaversConfiguration {
    public static final String MAPPING_STYLE_PROPERTY_NAME = "mappingStyle";

    private Properties properties;

    //modeled properties
    private MappingStyle mappingStyle;

    /**
     * loads javers-default.properties
     */
    public JaversConfiguration() {
        this.properties = new Properties();
        loadDefaultJaversProperties();
        assemble();
    }

    /**
     * @return never returns null
     */
    public MappingStyle getMappingStyle() {
        return mappingStyle;
    }

    /**
     * @return never returns null
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @throws IllegalArgumentException when given mappingStyle is null
     */
    public JaversConfiguration withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        this.mappingStyle = mappingStyle;
        return this;
    }

    //-- private

    private void loadDefaultJaversProperties() {
        PropertiesUtil.loadProperties("javers-default.properties", properties);
    }

    /**
     * loads modeled enum properties like mappingStyle
     * from String properties bag
     */
    private void assemble() {
        assembleMappingStyle();
    }

    private void assembleMappingStyle() {
        mappingStyle = PropertiesUtil.getEnumProperty(properties,MAPPING_STYLE_PROPERTY_NAME,MappingStyle.class);
    }


}
