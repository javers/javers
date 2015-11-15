package org.javers.core.examples.typeNames;

import org.javers.core.metamodel.annotation.Id;

/**
 * @author bartosz.walacik
 */
public class EntityWithRefactoredValueObject {
    @Id
    private int id;
    private AbstractValueObject value;
}
