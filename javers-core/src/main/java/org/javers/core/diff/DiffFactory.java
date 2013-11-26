package org.javers.core.diff;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.javers.core.diff.appenders.NewObjectAppender;
import org.javers.core.diff.appenders.NodeChangeAppender;
import org.javers.core.diff.appenders.ObjectRemovedAppender;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.model.domain.Diff;
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

    public DiffFactory(DFSGraphToSetConverter graphToSetConverter) {
        this.graphToSetConverter = graphToSetConverter;
        this.nodeChangeAppenders = Arrays.asList(new NewObjectAppender(), new ObjectRemovedAppender());
        this.propertyChangeAppender = Arrays.asList();
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

        //calculate property-to-property diff
        for (NodePair pair : nodeMatcher.match(leftGraph, rightGraph)) {
            List<Property> nodeProperties = pair.getEntity().getProperties();
            for (Property property : nodeProperties) {
                for (PropertyChangeAppender appender : propertyChangeAppender) { //this nested loops doesn't look good but unfortunately it is necessary
                    if (!appender.supports(property))  {
                        continue;
                    }
                    diff.addChanges((Collection)appender.calculateChanges(pair,property));
                }
            }
        }

        return diff;
    }
}
