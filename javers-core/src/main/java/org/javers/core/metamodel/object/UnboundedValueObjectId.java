package org.javers.core.metamodel.object;

import org.javers.core.metamodel.clazz.ValueObject;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Unbounded unwrap object, has '/' as symbolic cdoId representation.
 * <p/>
 * This kind of Id is assigned by graph builder to unwrap object which is not embedded in any Entity instance.
 * (by design or by accident)
 * <p/>
 *
 * Its recommended to avoid Unbounded unwrap objects since they don't have real global id.
 * Prefer embedding unwrap objects in Entity instances to leverage {@link ValueObjectId} global Id.
 *
 *
 * @author bartosz walacik
 */
public class UnboundedValueObjectId extends GlobalId {
    private transient final ValueObject valueObject;

    public UnboundedValueObjectId(ValueObject valueObject) {
        argumentIsNotNull(valueObject);
        this.valueObject = valueObject;
    }

    @Override
    public ValueObject getCdoClass() {
        return valueObject;
    }

    @Override
    public String value() {
        return valueObject.getClientsClass().getName()+"/";
    }

    @Override
    public Object getCdoId() {
        return "/";
    }
}
