package org.javers.model.object.graph;

import org.javers.model.mapping.ManagedClassFactory;
import org.javers.core.metamodel.property.FieldBasedPropertyScanner;
import org.junit.Before;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    @Before
    public void setUp() {
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner();
        ManagedClassFactory ef = new ManagedClassFactory(scanner,mapper);
        buildEntityManager(ef);
    }

}
