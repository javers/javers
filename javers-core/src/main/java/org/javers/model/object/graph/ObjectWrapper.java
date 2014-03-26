package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Wrapper for live client's domain object (aka CDO)
 *
 * @author bartosz walacik
 */
@Deprecated
public class ObjectWrapper{
    private final Cdo cdo;
    private final Map<Property, Edge> edges;


    public ObjectWrapper(Cdo cdo) {
        argumentIsNotNull(cdo);
        this.cdo = cdo;
        this.edges = new HashMap<>();
    }

    /**
     * @return never returns null
     */
    public Object unwrapCdo() {
        return cdo.getWrappedCdo();
    }

    public Cdo getCdo() {
        return cdo;
    }

        public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        return property.get(unwrapCdo());
    }

        public GlobalCdoId getGlobalCdoId() {
        return cdo.getGlobalId();
    }

        public ManagedClass getManagedClass() {
        return cdo.getManagedClass();
    }

        public List<Edge> getEdges() {
        return new ArrayList<>(edges.values());
    }

        public Edge getEdge(Property property) {
        return edges.get(property);
    }

    public void addEdge(Edge edge) {
        this.edges.put(edge.getProperty(), edge);
    }

        public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectWrapper that = (ObjectWrapper) o;
        return cdo.equals(that.cdo);
    }

        public int hashCode() {
        return cdo.hashCode();
    }

}
