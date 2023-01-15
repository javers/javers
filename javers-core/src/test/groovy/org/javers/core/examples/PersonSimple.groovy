package org.javers.core.examples

import jakarta.persistence.Id

class PersonSimple {
    @Id
    int id

    String name
}
