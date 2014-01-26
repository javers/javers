package org.javers.core.diff;

import java.util.Collection;
import java.util.List;

import org.javers.common.validation.Validate;
import org.javers.core.diff.appenders.*;
import org.javers.core.metamodel.property.Property;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.object.graph.ObjectNode;

/**
 * @author Maciej Zasada
 * @author Bartosz Walacik
 */
public class DiffFactory {

    private NodeMatcher nodeMatcher;
    private DFSGraphToSetConverter graphToSetConverter;
    private TypeMapper typeMapper;
    private List<NodeChangeAppender> nodeChangeAppenders;
    private List<PropertyChangeAppender> propertyChangeAppender;

    public DiffFactory(List<NodeChangeAppender> nodeChangeAppenders,
                       List<PropertyChangeAppender> propertyChangeAppender,
                       TypeMapper typeMapper) {
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.propertyChangeAppender = propertyChangeAppender;
        this.nodeMatcher = new NodeMatcher();
        this.typeMapper = typeMapper;
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

        //calculate snapshot of NewObjects
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
            JaversType javersType = typeMapper.getPropertyType(property);

            //optimization, skip all Appenders if null on both sides
            if (pair.isNullOnBothSides(property)) {
                continue;
            }

            for (PropertyChangeAppender appender : propertyChangeAppender) { //this nested loops doesn't look good but unfortunately it is necessary
                Collection<Change> changes = appender.calculateChangesIfSupported(pair, property, javersType);
                for (Change change : changes) {
                    diff.addChange(change, pair.getRight().getCdo().getWrappedCdo());
                }
            }
        }
    }
}
