package org.javers.core.diff;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.javers.common.collections.Sets;
import org.javers.core.diff.appenders.*;
import org.javers.model.mapping.Property;
import org.javers.model.object.graph.ObjectNode;

/**
 * @author Maciej Zasada
 * @author Bartosz Walacik
 */
public class DiffFactory {

    private NodeMatcher nodeMatcher;
    private DFSGraphToSetConverter graphToSetConverter;
    private List<NodeChangeAppender> nodeChangeAppenders;
    private List<PropertyChangeAppender> propertyChangeAppender;

    public DiffFactory(DFSGraphToSetConverter graphToSetConverter,
                       List<NodeChangeAppender> nodeChangeAppenders,
                       List<PropertyChangeAppender> propertyChangeAppender) {
        this.graphToSetConverter = graphToSetConverter;
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.propertyChangeAppender = propertyChangeAppender;
        this.nodeMatcher = new NodeMatcher();
    }

    public Diff create(String userId, ObjectNode leftRoot, ObjectNode rightRoot) {
        Diff diff = new Diff(userId);
        Set<ObjectNode> leftGraph = graphToSetConverter.convertFromGraph(leftRoot);
        Set<ObjectNode> rightGraph = graphToSetConverter.convertFromGraph(rightRoot);

        //calculate node scope diff
        for (NodeChangeAppender appender : nodeChangeAppenders) {
            diff.addChanges(appender.getChangeSet(leftGraph, rightGraph));
        }

        //TODO refactor - this difference is calculated also in NewObjectAppender
        for (ObjectNode node : Sets.difference(rightGraph, leftGraph)) {
            FakeNodePair pair = new FakeNodePair(node);
            appendPropertyChanges(diff, pair);
        }

        //calculate property-to-property diff
        for (NodePair pair : nodeMatcher.match(leftGraph, rightGraph)) {
            appendPropertyChanges(diff, pair);
        }

        //valueDehydrator.dehydrate(diff);

        return diff;
    }

    private void appendPropertyChanges(Diff diff, NodePair pair) {
        List<Property> nodeProperties = pair.getProperties();
        for (Property property : nodeProperties) {

            //optimization, skip all Appenders if null on both sides
            if (pair.isNullOnBothSides(property)) { continue;}

            for (PropertyChangeAppender appender : propertyChangeAppender) { //this nested loops doesn't look good but unfortunately it is necessary
                Collection<Change> changes = appender.calculateChangesIfSupported(pair,property);
                for (Change change : changes) {
                    diff.addChange(change, pair.getRight().getCdo().getWrappedCdo());
                }
            }
        }
    }
}
