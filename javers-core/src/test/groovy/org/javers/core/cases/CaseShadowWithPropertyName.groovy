package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.PropertyName
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification;

class CaseShadowWithPropertyName extends Specification {

    class Entity {
        @Id int id
        @PropertyName("otherField")
        String someField
    }

    def "should use @PropertyName when creating Shadows"(){
        given:
        def javers = JaversBuilder.javers().build()
        def e = new Entity(id:1, someField: "s")
        javers.commit("author", e)

        when:
        Entity shadow = javers.findShadows(QueryBuilder.byInstance(e).build()).get(0).get()

        then:
        shadow.id == 1
        shadow.someField == "s"
    }
}
