package org.javers.model.object.graph;

import org.javers.model.mapping.ManagedClassFactory;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class ObjectGraphFieldBuilderTest extends ObjectGraphBuilderTest {

    @Before
    public void setUp() {
        TypeMapper mapper = new TypeMapper();
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner(mapper);
        ManagedClassFactory ef = new ManagedClassFactory(scanner,mapper);
        entityManager = buildEntityManager(ef,mapper);
    }

}
