package org.javers.core.graph

import org.javers.core.metamodel.clazz.ClassAnnotationsScanner
import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.metamodel.clazz.ManagedClassFactory

/**
 * @author bartosz walacik
 */
class ObjectNodeBeanTest extends ObjectNodeTest{

    def setup() {
        def scanner = new BeanBasedPropertyScanner();
        managedClassFactory = new ManagedClassFactory(scanner, new ClassAnnotationsScanner());
    }
}
