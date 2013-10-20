package org.javers.core.diff;

import java.util.LinkedHashSet;
import java.util.Set;

import org.javers.common.collections.Function;
import org.javers.common.collections.Sets;
import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.changeType.NewObject;
import org.javers.model.domain.changeType.ObjectRemoved;
import org.javers.model.object.graph.ObjectNode;

/**
 * @author Maciej Zasada
 */
public class ChangeSetBuilder {

    private GraphToSetConverter graphToSetConverter;

    public ChangeSetBuilder(GraphToSetConverter graphToSetConverter) {
        this.graphToSetConverter = graphToSetConverter;
    }

    public Set<Change> build(ObjectNode previousRevision, ObjectNode currentRevision) {
        Set<Change> difference = new LinkedHashSet<>();
        Set<ObjectNode> previousGraph = graphToSetConverter.convertFromGraph(previousRevision);
        Set<ObjectNode> currentGraph = graphToSetConverter.convertFromGraph(currentRevision);

        difference.addAll(getRemovedObjects(previousGraph, currentGraph));
        difference.addAll(getNewObjects(previousGraph, currentGraph));

        return difference;
    }

    private Set<ObjectRemoved> getRemovedObjects(Set<ObjectNode> previousGraph, Set<ObjectNode> currentGraph) {
        Set<ObjectNode> removedObjectNodes = Sets.difference(previousGraph, currentGraph);
        return Sets.transform(removedObjectNodes, new Function<ObjectNode, ObjectRemoved>() {
            @Override
            public ObjectRemoved apply(ObjectNode input) {
                return new ObjectRemoved(createFromObjectNode(input));
            }
        });
    }

    private Set<NewObject> getNewObjects(Set<ObjectNode> previousGraph, Set<ObjectNode> currentGraph) {
        Set<ObjectNode> newObjectNodes = Sets.difference(currentGraph, previousGraph);
        return Sets.transform(newObjectNodes, new Function<ObjectNode, NewObject>() {
            @Override
            public NewObject apply(ObjectNode input) {
                return new NewObject(createFromObjectNode(input));
            }
        });
    }

    private GlobalCdoId createFromObjectNode(ObjectNode objectNode) {
        return new GlobalCdoId(objectNode.getEntity(), objectNode.getCdoId());
    }
}
