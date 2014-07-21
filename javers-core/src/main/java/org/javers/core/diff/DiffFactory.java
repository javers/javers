package org.javers.core.diff;

import org.javers.common.collections.Consumer;
import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.GraphFactory;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.appenders.NodeChangeAppender;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

import static org.javers.core.diff.DiffBuilder.diff;

/**
 * @author Maciej Zasada
 * @author Bartosz Walacik
 */
public class DiffFactory {

    private final NodeMatcher nodeMatcher = new NodeMatcher();
    private final TypeMapper typeMapper;
    private final List<NodeChangeAppender> nodeChangeAppenders;
    private final List<PropertyChangeAppender> propertyChangeAppender;
    private final GraphFactory graphFactory;

    public DiffFactory(TypeMapper typeMapper, List<NodeChangeAppender> nodeChangeAppenders, List<PropertyChangeAppender> propertyChangeAppender, GraphFactory graphFactory) {
        this.typeMapper = typeMapper;
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.propertyChangeAppender = propertyChangeAppender;
        this.graphFactory = graphFactory;
    }

    /**
     * @see Javers#initial(Object)
     */
    public Diff initial(Object newDomainObject) {
        return createInitial(buildGraph(newDomainObject));
    }

    /**
     * @see Javers#compare(Object, Object)
     */
    public Diff compare(Object oldVersion, Object currentVersion) {
        return create(buildGraph(oldVersion), buildGraph(currentVersion), Optional.<CommitMetadata>empty());
    }

    public Diff create(ObjectGraph leftGraph, ObjectGraph rightGraph, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(leftGraph, rightGraph);

        GraphPair graphPair = new GraphPair(leftGraph, rightGraph);
        return createAndAppendChanges(graphPair, commitMetadata);
    }

    public Diff createInitial(ObjectGraph currentGraph) {
        Validate.argumentIsNotNull(currentGraph);

        GraphPair graphPair = new GraphPair(currentGraph);
        return createAndAppendChanges(graphPair, Optional.<CommitMetadata>empty());
    }

    private LiveGraph buildGraph(Object handle) {
        return graphFactory.createLiveGraph(handle);
    }

    /**
     * Graph scope appender
     */
    private Diff createAndAppendChanges(GraphPair graphPair, Optional<CommitMetadata> commitMetadata) {
        DiffBuilder diff = diff();

        //calculate node scope diff
        for (NodeChangeAppender appender : nodeChangeAppenders) {
            diff.addChanges(appender.getChangeSet(graphPair), commitMetadata);
        }

        //calculate snapshot of NewObjects
        for (ObjectNode node : graphPair.getOnlyOnRight()) {
            FakeNodePair pair = new FakeNodePair(node);
            appendPropertyChanges(diff, pair, commitMetadata);
        }

        //calculate property-to-property diff
        for (NodePair pair : nodeMatcher.match(graphPair)) {
            appendPropertyChanges(diff, pair, commitMetadata);
        }

        return diff.build();
    }

    /* Node scope appender */
    private void appendPropertyChanges(DiffBuilder diff, NodePair pair, final Optional<CommitMetadata> commitMetadata) {
        List<Property> nodeProperties = pair.getProperties();
        for (Property property : nodeProperties) {

            //optimization, skip all appenders if null on both sides
            if (pair.isNullOnBothSides(property)) {
                continue;
            }

            JaversType javersType = typeMapper.getPropertyType(property);

            if (commitMetadata.isPresent()) {
                appendChanges(diff, pair, property, javersType, new Consumer<Change>() {
                    @Override
                    public void consume(Change change) {
                        change.bindToCommit(commitMetadata.get());
                    }
                });
            } else {
                appendChanges(diff, pair, property, javersType, new Consumer<Change>() {
                    @Override
                    public void consume(Change o) {

                    }
                });
            }
        }
    }

    private void appendChanges(DiffBuilder diff, NodePair pair, Property property, JaversType javersType, Consumer<Change> consumer) {
        for (PropertyChangeAppender appender : propertyChangeAppender) { //this nested loops doesn't look good but unfortunately it is necessary
            Change change = appender.calculateChangesIfSupported(pair, property, javersType);
            if (change != null) {
                diff.addChange(change, pair.getRight().wrappedCdo());
                consumer.consume(change);
            }
        }
    }
}
