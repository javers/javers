package org.javers.core.metamodel.property
/**
 * @author bartosz walacik
 */
class EntityIdFromBeanTest extends EntityIdTest {

    def setup() {
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner()
        entityFactory = new ManagedClassFactory(scanner)
    }
}
