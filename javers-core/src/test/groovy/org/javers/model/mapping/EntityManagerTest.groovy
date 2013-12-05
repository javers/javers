package org.javers.model.mapping

import org.javers.core.exceptions.JaversException
import org.javers.core.exceptions.JaversExceptionCode
import org.javers.model.mapping.type.TypeMapper
import spock.lang.Specification

import static com.googlecode.catchexception.CatchException.catchException
import static com.googlecode.catchexception.CatchException.caughtException
import static org.javers.test.assertion.Assertions.assertThat


class EntityManagerTest extends Specification{

    EntityManager entityManager;

    def setup() {
        TypeMapper mapper = new TypeMapper();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityFactory entityFactory = new EntityFactory(scanner);
        ValueObjectFactory valueObjectFactory = new ValueObjectFactory();
        entityManager = new EntityManager(entityFactory, valueObjectFactory, mapper);
    }

    def "should throw exception when class is not instance of managed class"() {
        when:
        catchException(entityManager).getByClass(Integer.class);

        then:
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.CLASS_NOT_MANAGED);
    }
}
