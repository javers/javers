package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public interface ObjectGraph {
    Set<ObjectNode> nodes();
    ObjectNode root();
}
