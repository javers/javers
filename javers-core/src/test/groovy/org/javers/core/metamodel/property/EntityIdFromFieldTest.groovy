package org.javers.core.metamodel.property
/**
 * @author bartosz walacik
 */
class EntityIdFromFieldTest extends EntityIdTest {

    def setup() {
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner()
        entityFactory = new ManagedClassFactory(scanner)
    }
}
