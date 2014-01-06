package org.javers.model.visitors;

import org.javers.core.diff.Change;

/**
 * @author bartosz walacik
 */
public interface ChangeVisitor extends Visitor<Change> {
}
