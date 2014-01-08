package org.javers.core.diff;

import org.javers.common.validation.Validate;
import org.javers.model.domain.Cdo;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.Property;
import org.javers.model.object.graph.ObjectNode;

/**
 * holds two versions of the same {@link ObjectNode}
 *
 * @author bartosz walacik
 */
public class NodePair {
    private final ObjectNode left;
    private final ObjectNode right;

    public NodePair(ObjectNode left, ObjectNode right) {
        Validate.argumentsAreNotNull(left, right);
        Validate.argumentCheck(left.getGlobalCdoId().equals(right.getGlobalCdoId()),"left & right should refer to the same Cdo");
        this.left = left;
        this.right = right;
    }

    public boolean isNullOnBothSides(Property property) {
        return left.getPropertyValue(property) == null &&
               right.getPropertyValue(property) == null;
    }

    public Object getLeftPropertyValue(Property property) {
        return left.getPropertyValue(property);
    }

    public Object getRightPropertyValue(Property property) {
        return right.getPropertyValue(property);
    }

    public ObjectNode getLeft() {
        return left;
    }

    public ObjectNode getRight() {
        return right;
    }

    public GlobalCdoId getGlobalCdoId() {
        return left.getGlobalCdoId();
    }

    public Entity getEntity() {
        return left.getEntity();
    }
}
