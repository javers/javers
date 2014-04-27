package org.javers.core.diff.appenders;

import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;

import java.util.Set;

/**
 * Node scope change appender (NewObject & ObjectRemoved)
 */
public interface NodeChangeAppender {

   Set<Change> getChangeSet(GraphPair graphPair);

}
