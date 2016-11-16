package org.javers.core.metamodel.type

import org.javers.core.MappingStyle
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.model.Entity

/**
 * @author bartosz walacik
 */
class TypeFactoryBeanIdTest extends TypeFactoryIdTest {

    def setupSpec() {
        typeFactory = TypeFactoryTest.create(MappingStyle.BEAN)
    }

    def "should not fail for Entity annotated with @Id on an extended generic method"() {
        when:
        def entity = typeFactory.create(new EntityDefinition(Entity.class))

        then:
        entity.idProperty.name == 'id'
    }
}
