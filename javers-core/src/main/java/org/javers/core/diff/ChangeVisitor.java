package org.javers.core.diff;

import org.javers.common.patterns.visitors.Visitor;
import org.javers.core.diff.Change;

/**
 * @author bartosz walacik
 */
public interface ChangeVisitor extends Visitor<Change> {
}
