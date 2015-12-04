package org.javers.core.metamodel.object;

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

    public UnboundedValueObjectId(String typeName) {
        super(typeName);
    }

    @Override
    public String value() {
        return getTypeName()+UNBOUNDED_FRAGMENT;
    }
}
