package org.javers.core.diff;

import java.security.cert.CertPathValidatorResult;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
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

    public DiffFactory(List<NodeChangeAppender> nodeChangeAppenders,
                       List<PropertyChangeAppender> propertyChangeAppender) {
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.propertyChangeAppender = propertyChangeAppender;
        this.nodeMatcher = new NodeMatcher();
    }

    public Diff createInitial(String userId, ObjectNode root) {
        Validate.argumentIsNotNull(root);

        GraphPair graphPair = new GraphPair(root);
        return createAndAppendChanges(userId, graphPair);
    }

    public Diff create(String userId, ObjectNode leftRoot, ObjectNode rightRoot) {
        Validate.argumentsAreNotNull(leftRoot, rightRoot);

        GraphPair graphPair = new GraphPair(leftRoot,rightRoot);
        return createAndAppendChanges(userId, graphPair);
    }

    /** Graph scope appender */
    private Diff createAndAppendChanges(String userId, GraphPair graphPair) {
        Diff diff = new Diff(userId);

        //calculate node scope diff
        for (NodeChangeAppender appender : nodeChangeAppenders) {
            diff.addChanges(appender.getChangeSet(graphPair));
        }

        //calculate snapshot for NewObjects
        for (ObjectNode node : graphPair.getOnlyOnRight()) {
            FakeNodePair pair = new FakeNodePair(node);
            appendPropertyChanges(diff, pair);
        }

        //calculate property-to-property diff
        for (NodePair pair : nodeMatcher.match(graphPair)) {
            appendPropertyChanges(diff, pair);
        }

        return diff;
    }

    /* Node scope appender */
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
