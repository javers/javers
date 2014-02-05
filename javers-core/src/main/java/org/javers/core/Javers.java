package org.javers.core;

import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;


/**
 * Facade to JaVers instance.
 * Should be constructed by {@link JaversBuilder} provided with your domain model metadata and configuration.
 * <br/>
 *
 * Domain TODO: move to doc
 * <ul>
 *   <li>Entity - a class in client's domain model. List of those classes should be provided to JaversBuilder</li>
 *   <li>CDO - client's domain object, instance of an Entity</li>
 * </ul>
 *
 * @author bartosz walacik
 */
public class Javers {
    private DiffFactory diffFactory;

    private TypeMapper typeMapper;

    private JsonConverter jsonConverter;

    /**
     * JaVers instance should be constructed by {@link JaversBuilder}
     */
    public Javers(DiffFactory diffFactory, TypeMapper typeMapper, JsonConverter jsonConverter) {
        this.diffFactory = diffFactory;
        this.typeMapper = typeMapper;
        this.jsonConverter = jsonConverter;
    }

    public Diff initial(String user, Object root) {
        ObjectGraphBuilder graph = new ObjectGraphBuilder(typeMapper);
        return diffFactory.createInitial(user, graph.buildGraph(root));
    }

    public Diff compare(String user, Object left, Object right) {
        ObjectGraphBuilder leftGraph = new ObjectGraphBuilder(typeMapper);
        ObjectGraphBuilder rightGraph = new ObjectGraphBuilder(typeMapper);
        return diffFactory.create(user, leftGraph.buildGraph(left), rightGraph.buildGraph(right));
    }

    public String toJson(Diff diff) {
        return jsonConverter.toJson(diff);
    }


    JaversType getForClass(Class<?> clazz) {
        return typeMapper.getJaversType(clazz);
    }
}
