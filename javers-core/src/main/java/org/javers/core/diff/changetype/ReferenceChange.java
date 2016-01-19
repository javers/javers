package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalId;

import static org.javers.common.string.ToStringBuilder.addField;

import org.javers.common.collections.Optional;

/**
 * Changed reference to Entity or ValueObject (in *ToOne relation)
 *
 * @author bartosz walacik
 */
public final class ReferenceChange extends PropertyChange {
    private final GlobalId left;
    private final GlobalId right;
    private transient final Optional<Object> leftObject;
    private transient final Optional<Object> rightObject;
    
    public ReferenceChange(GlobalId affectedCdoId, String propertyName, GlobalId leftReference,
            GlobalId rightReference ){
    	this( affectedCdoId, propertyName, leftReference, rightReference, null, null );
    }

    public ReferenceChange(GlobalId affectedCdoId, String propertyName, GlobalId leftReference,
                           GlobalId rightReference, Object leftObject, Object rightObject ) {
        super(affectedCdoId, propertyName);
        this.left = leftReference;
        this.right = rightReference;
        this.leftObject = Optional.fromNullable(leftObject);
        this.rightObject = Optional.fromNullable(rightObject);
    }

    /**
     * GlobalId of left (or previous) domain object reference
     */
    public GlobalId getLeft() {
        return left;
    }

    /**
     * GlobalId of right (or current) domain object reference
     */
    public GlobalId getRight() {
        return right;
    }

    /**
     * Domain object reference at left side of a diff.
     *<br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from JaversRepository
     */
    public Optional<Object> getLeftObject() {
    	return leftObject;
    }

    /**
     * Domain object reference at right side of a diff.
     *<br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from JaversRepository
     */
    public Optional<Object> getRightObject() {
    	return rightObject;
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + addField("oldRef", left) + addField("newRef", right);
    }
}
