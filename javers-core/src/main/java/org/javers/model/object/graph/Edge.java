package org.javers.model.object.graph;

import org.javers.model.mapping.Property;

/**
 * Relation between (Entity) instances
 *
 * @author bartosz walacik
 */
public interface Edge {
    Property getProperty();
}
