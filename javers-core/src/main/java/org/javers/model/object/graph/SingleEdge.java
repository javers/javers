package org.javers.model.object.graph;

/**
 * OneToOne or ManyToOne relation
 * @author bartosz walacik
 */
public interface SingleEdge extends Edge {
    ObjectNode getReference();
}
