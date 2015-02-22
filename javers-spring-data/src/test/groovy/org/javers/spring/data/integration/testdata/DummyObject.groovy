package org.javers.spring.data.integration.testdata

import org.javers.core.metamodel.annotation.Id

/**
 * Created by gessnerfl on 21.02.15.
 */
class DummyObject {
    @Id
    String id;
    String name;
}
