package org.javers.core.metamodel.type;

/**
 * @author bartosz walacik
 */
public class MapContentType {
    private final JaversType keyType;
    private final JaversType valueType;

    public MapContentType(JaversType keyType, JaversType valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public JaversType getKeyType() {
        return keyType;
    }

    public JaversType getValueType() {
        return valueType;
    }
}
