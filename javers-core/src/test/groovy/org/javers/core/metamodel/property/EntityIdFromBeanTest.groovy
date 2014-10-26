package org.javers.core.metamodel.property

import org.javers.core.metamodel.clazz.ClassAnnotationsScanner
import org.javers.core.metamodel.clazz.ManagedClassFactory

/**
 * @author bartosz walacik
 */
class EntityIdFromBeanTest extends EntityIdTest {

    def setup() {
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner()
        entityFactory = new ManagedClassFactory(scanner, new ClassAnnotationsScanner())
    }
}
