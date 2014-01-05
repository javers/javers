package org.javers.model.visitors;

import org.javers.model.domain.Change;
import org.javers.model.visitors.Visitor;

/**
 * @author bartosz walacik
 */
public interface ChangeVisitor extends Visitor<Change> {
}
