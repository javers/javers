package org.javers.core.graph

import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.metamodel.property.ManagedClassFactory

/**
 * @author bartosz walacik
 */
class ObjectNodeBeanTest extends ObjectNodeTest{

    def setup() {
        def scanner = new BeanBasedPropertyScanner();
        managedClassFactory = new ManagedClassFactory(scanner);
    }
}
