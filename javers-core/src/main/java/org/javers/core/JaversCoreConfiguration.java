package org.javers.core;

import org.javers.common.validation.Validate;

/**
 * @author bartosz walacik
 */
public class JaversCoreConfiguration extends AbstractConfiguration {
    public static final String MAPPING_STYLE_PROPERTY_NAME = "core.mappingStyle";

    //enum properties
    private MappingStyle mappingStyle;

    /**
     * loads javers-default.properties
     */
    public JaversCoreConfiguration() {
        propertyConfiguration = new PropertyConfiguration("javers-default.properties");
        assemble();
    }

    /**
     * @return never returns null
     */
    public MappingStyle getMappingStyle() {
        return mappingStyle;
    }

    public JaversCoreConfiguration withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        this.mappingStyle = mappingStyle;
        return this;
    }

    @Override
    protected void assemble() {
        if (containsPropertyKey(MAPPING_STYLE_PROPERTY_NAME)) {
            mappingStyle = getEnumProperty(MAPPING_STYLE_PROPERTY_NAME, MappingStyle.class);
        }
    }
}
