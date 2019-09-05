package org.javers.spring.example;

import org.javers.core.metamodel.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class DummyObject {
    @Id
    String id;
    String name;

    public Object getName() {
        return name;
    }
}
