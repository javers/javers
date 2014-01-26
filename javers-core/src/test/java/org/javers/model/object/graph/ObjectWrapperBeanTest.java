package org.javers.model.object.graph;

import org.javers.core.metamodel.property.BeanBasedPropertyScanner;
import org.javers.model.mapping.ManagedClassFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class ObjectWrapperBeanTest extends ObjectWrapperTest {

    @Before
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner();
        managedClassFactory = new ManagedClassFactory(scanner,mapper);
    }
}
