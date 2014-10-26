package org.javers.core;

import org.javers.common.validation.Validate;

/**
 * @author bartosz walacik
 */
public class JaversCoreConfiguration {

    private MappingStyle mappingStyle = MappingStyle.FIELD;

    private boolean newObjectsSnapshot = false;

    /**
     * loads javers-default.properties
     */
    public JaversCoreConfiguration() {
    }

    /**
     * @return never returns null
     */
    public MappingStyle getMappingStyle() {
        return mappingStyle;
    }

    public boolean isNewObjectsSnapshot() {
        return newObjectsSnapshot;
    }

    public JaversCoreConfiguration withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        this.mappingStyle = mappingStyle;
        return this;
    }

    public JaversCoreConfiguration withNewObjectsSnapshot(boolean newObjectsSnapshot) {
        this.newObjectsSnapshot = newObjectsSnapshot;
        return this;
    }

    /*@Override
    protected void assemble() {
        mappingStyle = getEnumProperty("core.mappingStyle", MappingStyle.class);

        newObjectsSnapshot = getBooleanProperty("core.diff.newObjectsSnapshot");
    }*/
}
