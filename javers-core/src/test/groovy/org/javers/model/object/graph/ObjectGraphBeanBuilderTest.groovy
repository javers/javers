package org.javers.model.object.graph

import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.metamodel.property.ManagedClassFactory

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    def setup() {
        def scanner = new BeanBasedPropertyScanner();
        def managedClassFactory = new ManagedClassFactory(scanner);
        buildEntityManager(managedClassFactory);
    }
}
