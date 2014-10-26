package org.javers.core.metamodel.property

import org.javers.core.metamodel.clazz.ClassAnnotationsScanner
import org.javers.core.metamodel.clazz.ManagedClassFactory

/**
 * @author bartosz walacik
 */
class EntityIdFromFieldTest extends EntityIdTest {

    def setup() {
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner()
        entityFactory = new ManagedClassFactory(scanner, new ClassAnnotationsScanner())
    }
}
