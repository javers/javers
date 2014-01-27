package org.javers.core.metamodel.property
/**
 * @author Pawel Cierpiatka
 */
class EntityFromBeanConstructionTest extends EntityConstructionTest {

    def setupSpec() {
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner();
        entityFactory = new ManagedClassFactory(scanner);
    }

}