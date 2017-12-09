package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.core.metamodel.type.EntityType;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Entity instance global identifier, consists of Entity reference and cdoId
 *
 * @author bartosz walacik
 */
public class InstanceId extends GlobalId {
    private final Object cdoId;
    private final String cdoIdAsString;

    InstanceId(String typeName, Object cdoId) {
        super(typeName);
        argumentsAreNotNull(cdoId);
        this.cdoId = cdoId;
        this.cdoIdAsString = cdoId.toString();
    }

    public InstanceId(String typeName, Object cdoId, String cdoIdAsString) {
        super(typeName);
        argumentsAreNotNull(cdoId, cdoIdAsString);
        this.cdoId = cdoId;
        this.cdoIdAsString = cdoIdAsString;
    }

    /**
     * Identifier of (client's) Entity <b>instance</b>, should be unique in Entity scope.
     * For example database primary key or any domain identifier like user.login
     */
    public Object getCdoId() {
        return cdoId;
    }

    public String value() {
        return getTypeName()+"/"+cdoIdAsString;
    }

    @Override
    public String toString() {
        return getTypeNameShort()+"/"+cdoIdAsString;
    }
}
