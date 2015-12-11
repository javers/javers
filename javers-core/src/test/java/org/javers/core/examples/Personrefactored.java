package org.javers.core.examples;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

@TypeName("Person")
class PersonRefactored {
    @Id
    private int id;

    private String name;

    private String city;

    PersonRefactored(int id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }
}
