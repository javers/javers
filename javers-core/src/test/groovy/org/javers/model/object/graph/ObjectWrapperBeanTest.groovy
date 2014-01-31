package org.javers.model.object.graph

import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.metamodel.property.ManagedClassFactory

/**
 * @author bartosz walacik
 */
class ObjectWrapperBeanTest extends ObjectWrapperTest{

    def setup() {
        def scanner = new BeanBasedPropertyScanner();
        managedClassFactory = new ManagedClassFactory(scanner);
    }
}
