package org.javers.model.object.graph;

import org.javers.model.visitors.Visitable;

/**
 * Relation between (Entity) instances
 * <br/>
 * Immutable
 *
 * @author bartosz walacik
 */
public abstract class Edge implements Visitable<GraphVisitor> {

    protected Edge() {
    }
}
