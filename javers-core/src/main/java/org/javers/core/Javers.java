package org.javers.core;

import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;


/**
 * Facade to JaVers instance.<br/>
 * Should be constructed by {@link JaversBuilder} provided with your domain specific configuration.
 * <br/><br/>
 *
 * See {@link MappingDocumentation} to find out how to map your domain model
 *
 * @author bartosz walacik
 */
public class Javers {
    private final DiffFactory diffFactory;

    private final TypeMapper typeMapper;

    private final JsonConverter jsonConverter;

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
