package org.javers.model.object.graph;

import org.javers.core.metamodel.property.BeanBasedPropertyScanner;
import org.javers.model.mapping.ManagedClassFactory;
import org.junit.Before;

/**
 * @author Pawel Cierpiatka
 */
public class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    @Before
    public void setUp() {
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner();
        ManagedClassFactory ef = new ManagedClassFactory(scanner,mapper);
        buildEntityManager(ef);
    }



}
