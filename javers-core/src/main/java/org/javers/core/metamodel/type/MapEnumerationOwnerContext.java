package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.OwnerContext;

/**
 * @author bartosz.walacik
 */
public class MapEnumerationOwnerContext extends EnumerationAwareOwnerContext {
    private Object key;
    private boolean isKey;

    private final JaversType keyType;
    private final JaversType valueType;

    public static MapEnumerationOwnerContext dummy(KeyValueType keyValueType) {
            return new MapEnumerationOwnerContext(keyValueType,
                new OwnerContext() {
                public GlobalId getOwnerId() {
                    throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
                }

                public String getPath() {
                    throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
                }

                public boolean requiresObjectHasher() {
                    return false;
                }
            });
    }

    public MapEnumerationOwnerContext(KeyValueType keyValueType, OwnerContext ownerContext) {
        this(keyValueType, ownerContext, false);
    }

    public MapEnumerationOwnerContext(KeyValueType keyValueType, OwnerContext ownerContext, boolean requiresObjectHasher) {
        super(ownerContext, requiresObjectHasher);
        this.keyType = keyValueType.getKeyJaversType();
        this.valueType = keyValueType.getValueJaversType();
    }

    @Override
    public String getEnumeratorContextPath() {
        if (key != null) {
            return key.toString();
        }
        return "";
    }

    public boolean isKey() {
        return isKey;
    }

    public JaversType getCurrentType() {
        if (isKey) {
            return keyType;
        }
        return valueType;
    }

    public void switchToValue(Object key) {
        this.key = key;
        this.isKey = false;
    }

    public void switchToKey() {
        this.key = null;
        this.isKey = true;
    }
}
