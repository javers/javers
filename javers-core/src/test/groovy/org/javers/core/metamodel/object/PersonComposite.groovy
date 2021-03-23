package org.javers.core.metamodel.object

import groovy.transform.MapConstructor
import org.javers.core.metamodel.annotation.Id

import java.time.LocalDate

@MapConstructor
class PersonComposite {
    @Id String name
    @Id String surname
    @Id LocalDate dob
    int data

    @Id String getName() {
        return name
    }

    @Id String getSurname() {
        return surname
    }

    @Id LocalDate getDob() {
        return dob
    }

    int getData() {
        return data
    }
}