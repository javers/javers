package org.javers.model.object.graph

import org.javers.core.metamodel.property.FieldBasedPropertyScanner
import org.javers.core.metamodel.property.ManagedClassFactory

/**
 * @author Pawel Cierpiatka
 */
class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    def setup() {
        def scanner = new FieldBasedPropertyScanner()
        def ef = new ManagedClassFactory(scanner)
        buildEntityManager(ef);
    }
}
