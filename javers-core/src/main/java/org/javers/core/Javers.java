package org.javers.core;

import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitFactory;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.graph.ObjectNode;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.repository.api.JaversRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    private static final Logger logger = LoggerFactory.getLogger(Javers.class);

    private final DiffFactory diffFactory;
    private final TypeMapper typeMapper;
    private final JsonConverter jsonConverter;
    private final CommitFactory commitFactory;
    private final JaversRepository repository;

    /**
     * JaVers instance should be constructed by {@link JaversBuilder}
     */
    public Javers(DiffFactory diffFactory, TypeMapper typeMapper, JsonConverter jsonConverter, CommitFactory commitFactory, JaversRepository repository) {
        this.diffFactory = diffFactory;
        this.typeMapper = typeMapper;
        this.jsonConverter = jsonConverter;
        this.commitFactory = commitFactory;
        this.repository = repository;
    }

    /**
     * <p>
     * Persists current version of given domain objects graph in JaVers repository.
     * All changes made on versioned objects are recorded,
     * new objects become versioned and its initial state is recorded.
     * </p>
     *
     * For any versioned object, you can:
     * <ul>
     *     <li/>  TODO
     * </ul>
     *
     * @param currentVersion domain object, instance of Entity or ValueObject.
     *        It should be root of an aggregate, tree root
     *        or any node in objects graph from where all other nodes are navigable.
     *        (Javadoc source: {@link ObjectGraphBuilder#buildGraph(Object)})
     */
    public Commit commit(String author, Object currentVersion) {
        Commit commit = commitFactory.create(author, currentVersion);

        repository.persist(commit);

        logger.info(commit.shortDesc());

        return commit;
    }

    /**
     * <p>
     * Easiest way to calculate diff, just provide two versions of the same object graph.
     * Use it if you don't want to store domain objects history in JaVers repository.
     * </p>
     *
     * <p>
     * Diffs can be converted to JSON with {@link #toJson(Diff)} and stored in custom repository
     * </p>
     */
    public Diff compare(String user, Object oldVersion, Object currentVersion) {
        return diffFactory.compare(user, oldVersion, currentVersion);
    }

    /**
     * Initial diff is a kind of snapshot of given domain objects graph.
     * Use it alongside with {@link #compare(String, Object, Object)}
     */
    public Diff initial(String user, Object newDomainObject) {
        return diffFactory.initial(user, newDomainObject);
    }


    /**
     * Use it alongside with {@link #compare(String, Object, Object)}
     */
    public String toJson(Diff diff) {
        return jsonConverter.toJson(diff);
    }

    public IdBuilder idBuilder() {
        return new IdBuilder(new GlobalIdFactory(typeMapper));
    }

    JaversType getForClass(Class<?> clazz) {
        return typeMapper.getJaversType(clazz);
    }
}
