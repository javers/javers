package org.javers.core.metamodel.property
/**
 * @author Pawel Cierpiatka
 */
class EntityFromFieldsConstructionTest extends EntityConstructionTest {

    def setupSpec() {
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner();
        entityFactory = new ManagedClassFactory(scanner);
    }
}
