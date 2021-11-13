package org.javers.spring.auditable.integration

import org.javers.core.metamodel.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class DummyObject {
    @Id
    String id
    String name

    Object getName() {
        name
    }
}
