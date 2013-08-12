package org.javers.model.object.graph;

import java.util.List;
/**
 * OneToMany or ManyToMany relation
 * @author bartosz walacik
 */
public interface MultiEdge extends Edge {
    List<ObjectNode> getListOfReferences();
}
