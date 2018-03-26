package org.javers.core.diff;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.appenders.NodeChangeAppender;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Maciej Zasada
 * @author Bartosz Walacik
 */
public class DiffFactory {

    private final NodeMatcher nodeMatcher = new NodeMatcher();
    private final TypeMapper typeMapper;
    private final List<NodeChangeAppender> nodeChangeAppenders;
    private final List<PropertyChangeAppender> propertyChangeAppender;
    private final LiveGraphFactory graphFactory;
    private final JaversCoreConfiguration javersCoreConfiguration;

    public DiffFactory(TypeMapper typeMapper, List<NodeChangeAppender> nodeChangeAppenders, List<PropertyChangeAppender> propertyChangeAppender, LiveGraphFactory graphFactory, JaversCoreConfiguration javersCoreConfiguration) {
        this.typeMapper = typeMapper;
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.graphFactory = graphFactory;
        this.javersCoreConfiguration = javersCoreConfiguration;

        //sort by priority
        Collections.sort(propertyChangeAppender, (p1, p2) -> ((Integer)p1.priority()).compareTo(p2.priority()));
        this.propertyChangeAppender = propertyChangeAppender;
    }

    /**
     * @see Javers#compare(Object, Object)
     */
    public Diff compare(Object oldVersion, Object currentVersion) {
        return create(buildGraph(oldVersion), buildGraph(currentVersion), Optional.<CommitMetadata>empty());
    }

    public <T> Diff compareCollections(Collection<T> oldVersion, Collection<T> currentVersion, Class<T> itemClass) {
        return create(buildGraph(oldVersion, itemClass), buildGraph(currentVersion, itemClass), Optional.<CommitMetadata>empty());
    }

    private LiveGraph buildGraph(Collection handle, Class itemClass) {
        return graphFactory.createLiveGraph(handle, itemClass);
    }

    public Diff create(ObjectGraph leftGraph, ObjectGraph rightGraph, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(leftGraph, rightGraph);

        GraphPair graphPair = new GraphPair(leftGraph, rightGraph);
        return createAndAppendChanges(graphPair, commitMetadata);
    }

    public Diff singleTerminal(GlobalId removedId, CommitMetadata commitMetadata){
        Validate.argumentsAreNotNull(removedId, commitMetadata);

        DiffBuilder diff = new DiffBuilder(javersCoreConfiguration.getPrettyValuePrinter());
        diff.addChange(new ObjectRemoved(removedId, empty(), of(commitMetadata)));

        return diff.build();
    }

    /**
     * @param newDomainObject object or handle to object graph
     */
    public Diff initial(Object newDomainObject) {
        Validate.argumentIsNotNull(newDomainObject);

        ObjectGraph currentGraph = buildGraph(newDomainObject);

        GraphPair graphPair = new GraphPair(currentGraph);
        return createAndAppendChanges(graphPair, empty());
    }

    private LiveGraph buildGraph(Object handle) {
        JaversType jType = typeMapper.getJaversType(handle.getClass());
        if (jType instanceof ValueType || jType instanceof PrimitiveType){
            throw new JaversException(JaversExceptionCode.COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED,
                    jType.getClass().getSimpleName(), handle.getClass().getSimpleName());
        }
        return graphFactory.createLiveGraph(handle);
    }

    /**
     * Graph scope appender
     */
    private Diff createAndAppendChanges(GraphPair graphPair, Optional<CommitMetadata> commitMetadata) {
        DiffBuilder diff = new DiffBuilder(javersCoreConfiguration.getPrettyValuePrinter());

        //calculate node scope diff
        for (NodeChangeAppender appender : nodeChangeAppenders) {
            diff.addChanges(appender.getChangeSet(graphPair), commitMetadata);
        }

        //calculate snapshot of NewObjects
        if (javersCoreConfiguration.isNewObjectsSnapshot()) {
            for (ObjectNode node : graphPair.getOnlyOnRight()) {
                FakeNodePair pair = new FakeNodePair(node);
                appendPropertyChanges(diff, pair, commitMetadata);
            }
        }

        //calculate property-to-property diff
        for (NodePair pair : nodeMatcher.match(graphPair)) {
            appendPropertyChanges(diff, pair, commitMetadata);
        }

        return diff.build();
    }

    /* Node scope appender */
    private void appendPropertyChanges(DiffBuilder diff, NodePair pair, final Optional<CommitMetadata> commitMetadata) {
        List<JaversProperty> nodeProperties = pair.getProperties();
        for (JaversProperty property : nodeProperties) {

            //optimization, skip all appenders if null on both sides
            if (pair.isNullOnBothSides(property)) {
                continue;
            }

            JaversType javersType = property.getType();

            appendChanges(diff, pair, property, javersType, commitMetadata);
        }
    }

    private void appendChanges(DiffBuilder diff, NodePair pair, JaversProperty property, JaversType javersType, Optional<CommitMetadata> commitMetadata) {
        for (PropertyChangeAppender appender : propertyChangeAppender) {
            if (! appender.supports(javersType)){
                continue;
            }

            final Change change = appender.calculateChanges(pair, property);
            if (change != null) {
                diff.addChange(change, pair.getRight().wrappedCdo());
                commitMetadata.ifPresent(cm -> change.bindToCommit(cm));
            }
            break;
        }
    }
}
