package org.javers.core.metamodel.object

import groovy.transform.MapConstructor
import org.javers.core.metamodel.annotation.Id

class PersonId {
    String name
    @Id int id
}

@MapConstructor
class PersonSimpleEntityId {
    @Id PersonId personId
    int data
}
