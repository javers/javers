package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import static org.javers.common.string.ToStringBuilder.addField;

import org.javers.common.collections.Optional;

/**
 * changed reference (in *ToOne relation)
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

    public GlobalId getLeft() {
        return left;
    }

    public GlobalId getRight() {
        return right;
    }
    
    public Optional<Object> getLeftObject() {
    	return leftObject;
    }
    
    public Optional<Object> getRightObject() {
    	return rightObject;
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + addField("oldRef", left) + addField("newRef", right);
    }
}
