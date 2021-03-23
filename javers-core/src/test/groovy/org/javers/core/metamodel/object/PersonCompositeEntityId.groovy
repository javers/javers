package org.javers.core.metamodel.object

import groovy.transform.MapConstructor
import org.javers.core.metamodel.annotation.Id

class FirstNameId {
    String name
    @Id int id
}

class LastNameId {
    String name
    @Id int id
}

@MapConstructor
class PersonCompositeEntityId {
    @Id FirstNameId firstNameId
    @Id LastNameId lastNameId
    int data
}
