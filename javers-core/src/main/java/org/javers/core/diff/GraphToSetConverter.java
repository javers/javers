package org.javers.core.diff;

import java.util.Set;

import org.javers.model.object.graph.ObjectNode;

/**
 * @author Maciej Zasada
 */
public interface GraphToSetConverter {

    Set<ObjectNode> convertFromGraph(ObjectNode graph);
}
