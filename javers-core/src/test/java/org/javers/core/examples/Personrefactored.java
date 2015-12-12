package org.javers.core.examples;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

@TypeName("Person")
class PersonRefactored {
    @Id
    private int id;

    private String name;

    private String city;
}
