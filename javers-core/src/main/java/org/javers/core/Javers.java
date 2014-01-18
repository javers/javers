package org.javers.core;

import org.javers.core.diff.DiffFactory;
import org.javers.core.json.JsonConverter;
import org.javers.core.diff.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.object.graph.Fake;
import org.javers.model.object.graph.ObjectGraphBuilder;
import org.javers.model.object.graph.ObjectNode;

import java.lang.reflect.Field;

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

    private EntityManager entityManager;

    private DiffFactory diffFactory;

    private ObjectGraphBuilder objectGraphBuilder;

    private JsonConverter jsonConverter;

    public Javers(EntityManager entityManager, DiffFactory diffFactory, ObjectGraphBuilder objectGraphBuilder, JsonConverter jsonConverter) {
        this.entityManager = entityManager;
        this.diffFactory = diffFactory;
        this.objectGraphBuilder = objectGraphBuilder;
        this.jsonConverter = jsonConverter;
    }

    /**
     * JaVers instance should be constructed by {@link JaversBuilder}
     */


    public ManagedClass getByClass(Class<?> forClass) {
        return entityManager.getByClass(forClass);
    }

    public boolean isManaged(Class<?> forClass) {
        return entityManager.isManaged(forClass);
    }

    public Diff compare(String user, Object left, Object right) {
        ObjectGraphBuilder leftGraph = new ObjectGraphBuilder(entityManager);
        ObjectGraphBuilder rightGraph = new ObjectGraphBuilder(entityManager);
        return diffFactory.create(user, leftGraph.buildGraph(left), rightGraph.buildGraph(right));
    }

    public String toJson(Diff diff) {
        return jsonConverter.toJson(diff);
    }

    public  Diff firstDiff(String user, Object object) {
        ObjectGraphBuilder rightGraph = new ObjectGraphBuilder(entityManager);
        ObjectNode rightNode = rightGraph.buildGraph(object);

        GlobalCdoId id = rightNode.getGlobalCdoId();
        Fake fake = new Fake(id);

        return diffFactory.create(user, fake, rightNode);
    }
}
