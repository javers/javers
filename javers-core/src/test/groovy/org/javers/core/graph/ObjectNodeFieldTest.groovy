package org.javers.core.graph

import org.javers.core.metamodel.clazz.ClassAnnotationsScanner
import org.javers.core.metamodel.property.FieldBasedPropertyScanner
import org.javers.core.metamodel.clazz.ManagedClassFactory

/**
 * @author bartosz walacik
 */
class ObjectNodeFieldTest extends ObjectNodeTest{

    def setup() {
        def scanner = new FieldBasedPropertyScanner();
        managedClassFactory = new ManagedClassFactory(scanner, new ClassAnnotationsScanner());
    }
}

