package org.javers.core.examples;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

@TypeName("org.javers.core.examples.PersonSimple")
class PersonRetrofitted {
    @Id
    private int id;

    private String name;

    PersonRetrofitted(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
