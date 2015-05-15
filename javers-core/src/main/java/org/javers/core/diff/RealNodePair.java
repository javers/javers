package org.javers.core.diff;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * holds two versions of the same {@link ObjectNode}
 *
 * @author bartosz walacik
 */
//TODO refactor -> extends FakeNodePair???
public class RealNodePair implements NodePair {
    private final ObjectNode left;
    private final ObjectNode right;

    public RealNodePair(ObjectNode left, ObjectNode right) {
        Validate.argumentsAreNotNull(left, right);
        Validate.argumentCheck(left.getGlobalId().equals(right.getGlobalId()),"left & right should refer to the same Cdo");
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isNullOnBothSides(Property property) {
        return left.getPropertyValue(property) == null &&
               right.getPropertyValue(property) == null;
    }

    @Override
    public Object getLeftPropertyValue(Property property) {
        return getPropertyValueEvenIfMissing(left, property);
    }

    @Override
    public Object getRightPropertyValue(Property property) {
        return getPropertyValueEvenIfMissing(right,property);
    }

    /**
     * Converts JaversException.MISSING_PROPERTY to null value
     */
    private Object getPropertyValueEvenIfMissing(ObjectNode source, Property property){
       try{
           return source.getPropertyValue(property);
       } catch (JaversException e){
           if (e.getCode() == JaversExceptionCode.MISSING_PROPERTY){
               return null;
           }
           throw e;
       }
    }

    @Override
    public GlobalId getRightGlobalId(Property property) {
        return right.getReference(property);
    }

    @Override
    public GlobalId getLeftGlobalId(Property property) {
        return left.getReference(property);
    }

    public ObjectNode getLeft() {
        return left;
    }

    @Override
    public ObjectNode getRight() {
        return right;
    }

    @Override
    public List<Property> getProperties() {
        return left.getManagedClass().getProperties();
    }

    @Override
    public GlobalId getGlobalId() {
        return left.getGlobalId();
    }

}
