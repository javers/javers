package org.javers.core.diff;

import java.util.List;
import java.util.Set;

import org.javers.core.diff.appenders.ChangeSetAppender;
import org.javers.model.domain.Diff;
import org.javers.model.object.graph.ObjectNode;

/**
 * @author Maciej Zasada
 */
public class DiffFactory {

  private GraphToSetConverter graphToSetConverter;
  private List<ChangeSetAppender> changeSetAppenders;

  public DiffFactory(GraphToSetConverter graphToSetConverter, List<ChangeSetAppender> changeSetAppenders) {
    this.graphToSetConverter = graphToSetConverter;
    this.changeSetAppenders = changeSetAppenders;
  }

  public Diff create(String userId, ObjectNode previousRevision, ObjectNode currentRevision) {
    Diff diff = new Diff(userId);
    Set<ObjectNode> previousGraph = graphToSetConverter.convertFromGraph(previousRevision);
    Set<ObjectNode> currentGraph = graphToSetConverter.convertFromGraph(currentRevision);

    for (ChangeSetAppender changeSetAppender : changeSetAppenders) {
      changeSetAppender.append(diff, previousGraph, currentGraph);
    }

    return diff;
  }
}
