package org.javers.core

import groovy.transform.TupleConstructor
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification
import spock.lang.Unroll

@TupleConstructor class GPerson {
    @Id int id
    String name;
}

/**
 * @author bartosz.walacik
 */
class JaversGroovyObjectTest extends Specification {

    @Unroll
    def "should support GroovyObjects in #mappingStyle mappingStyle"(){
        given:
        def javers = JaversBuilder.javers().withMappingStyle(mappingStyle).build()

        when:
        javers.commit('author', new GPerson(1,'bob'))
        javers.commit('author', new GPerson(1,'john'))
        def changes = javers.findChanges(QueryBuilder.byClass(GPerson).build())

        then:
        def jtype = javers.getTypeMapping(GPerson)
        jtype.properties.name as Set == ['id','name'] as Set

        changes.size() == 1
        changes[0].propertyName == 'name'

        where:
        mappingStyle << [MappingStyle.FIELD, MappingStyle.BEAN]
    }
}
