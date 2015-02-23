package org.javers.core.diff;

import org.javers.common.collections.Consumer;
import org.javers.common.collections.Optional;
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
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.Collections;
import java.util.Comparator;
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
    private final LiveGraphFactory graphFactory;
    private final JaversCoreConfiguration javersCoreConfiguration;

    public DiffFactory(TypeMapper typeMapper, List<NodeChangeAppender> nodeChangeAppenders, List<PropertyChangeAppender> propertyChangeAppender, LiveGraphFactory graphFactory, JaversCoreConfiguration javersCoreConfiguration) {
        this.typeMapper = typeMapper;
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.graphFactory = graphFactory;
        this.javersCoreConfiguration = javersCoreConfiguration;

        //sort by priority
        Collections.sort(propertyChangeAppender, new Comparator<PropertyChangeAppender>() {
            public int compare(PropertyChangeAppender p1, PropertyChangeAppender p2) {
                return ((Integer)p1.priority()).compareTo(p2.priority());
            }
        });
        this.propertyChangeAppender = propertyChangeAppender;
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

    public Diff singleTerminal(GlobalId removedId, CommitMetadata commitMetadata){
        Validate.argumentsAreNotNull(removedId, commitMetadata);

        DiffBuilder diff = diff();
        diff.addChange(new ObjectRemoved(removedId, Optional.empty(), commitMetadata));

        return diff.build();
    }

    /**
     * @param newDomainObject object or handle to object graph
     */
    public Diff initial(Object newDomainObject) {
        Validate.argumentIsNotNull(newDomainObject);

        ObjectGraph currentGraph = buildGraph(newDomainObject);

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
        List<Property> nodeProperties = pair.getProperties();
        for (Property property : nodeProperties) {

            //optimization, skip all appenders if null on both sides
            if (pair.isNullOnBothSides(property)) {
                continue;
            }

            JaversType javersType = typeMapper.getPropertyType(property);

            appendChanges(diff, pair, property, javersType, commitMetadata);
        }
    }

    private void appendChanges(DiffBuilder diff, NodePair pair, Property property, JaversType javersType, Optional<CommitMetadata> commitMetadata) {
        for (PropertyChangeAppender appender : propertyChangeAppender) {
            if (! appender.supports(javersType)){
                continue;
            }

            final Change change = appender.calculateChanges(pair, property);
            if (change != null) {
                diff.addChange(change, pair.getRight().wrappedCdo());

                commitMetadata.ifPresent(new Consumer<CommitMetadata>() {
                    public void consume(CommitMetadata cm) {
                        change.bindToCommit(cm);
                    }
                });
            }
            break;
        }
    }
}
