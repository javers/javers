package org.javers.spring.boot.mongo;

import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

@Entity
@TypeName("AnotherEntity")
public class AnotherEntity {
    @Id
    public int getId() {
        return 0;
    }
}
