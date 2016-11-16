package org.javers.core.metamodel.type

import org.javers.core.MappingStyle
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.clazz.EntityDefinition

/**
 * @author bartosz walacik
 */
class TypeFactoryBeanIdTest extends TypeFactoryIdTest {

    def setupSpec() {
        typeFactory = TypeFactoryTest.create(MappingStyle.BEAN)
    }


    abstract class AbstractEntity<ID extends Serializable> {
        abstract ID getId()
    }

    class Entity extends AbstractEntity<Long> {
        @Id
        @Override
        Long getId() { }

        void setId(Long id) { }
    }

    // see https://github.com/javers/javers/issues/457
    def "should not fail for Entity annotated with @Id on the extended generic method"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(Entity))

        then:
        entity.idProperty.name == 'id'
    }
}
