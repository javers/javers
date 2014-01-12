package org.javers.model.object.graph;

import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

/**
 * @author Pawel Cierpiatka
 */
public class ObjectGraphBeanBuilderTest extends ObjectGraphBuilderTest {

    @Before
    public void setUp() {
        TypeMapper mapper = new TypeMapper();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityFactory ef = new EntityFactory(scanner);
        entityManager = buildEntityManager(ef,mapper);
    }



}
