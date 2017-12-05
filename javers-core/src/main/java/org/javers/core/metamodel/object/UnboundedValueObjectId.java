package org.javers.core.metamodel.object;

import java.util.Map;
import java.util.function.Function;

/**
 * Unbounded ValueObject, has '/' as symbolic cdoId representation.
 * <p/>
 * This kind of Id is assigned by graph builder to ValueObject which is not embedded in any Entity instance.
 * (by design or by accident)
 * <p/>
 *
 * Its recommended to avoid Unbounded ValueObject since they don't have a real global id.
 * Prefer embedding ValueObject in Entity instances to leverage {@link ValueObjectId} global Id.
 *
 *
 * @author bartosz walacik
 */
public class UnboundedValueObjectId extends GlobalId {
    private static final String UNBOUNDED_FRAGMENT = "/";

    public UnboundedValueObjectId(String typeName, Map<Class, Function<Object, String>> mappedToStringFunction) {
        super(typeName, mappedToStringFunction);
    }

    @Override
    public String value() {
        return getTypeName()+UNBOUNDED_FRAGMENT;
    }
}
