package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ShallowReference
import org.javers.core.metamodel.clazz.EntityDefinitionBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class OpBoxCaseClassExtractionError extends Specification {
    interface ParamType {
        String getName()
    }

    class BooleanParamType implements ParamType {
        String name = "name"
        String getName() {
            name
        }
    }

    @ShallowReference
    class ParamPrototype<T extends ParamType> {
        @Id String name
        T type
        Optional<Object> defaultValue
    }

    class DataSource {
        String name
        ParamPrototype<? extends ParamType> paramPrototype
    }

    def "should not fail when spawning ShallowReferenceType from prototype"(){
        given:
        def javers = JaversBuilder.javers().build();
        def d1 = new DataSource(name: "name", paramPrototype: new ParamPrototype(name:"p1"))
        def d2 = new DataSource(name: "name", paramPrototype: new ParamPrototype(name:"p2"))

        when:
        def diff = javers.compare(d1, d2)

        then:
        diff.changes.size() == 1
    }

    def "should allow defining ShallowReference in JaversBuilder"(){
        given:
        def javers = JaversBuilder.javers()
            .registerEntity(EntityDefinitionBuilder.entityDefinition(DataSource).withShallowReference().withIdPropertyName("name").build())
            .build()

        expect:
        javers.getTypeMapping(DataSource).idProperty.name == "name"
        javers.getTypeMapping(DataSource).properties.size() == 0
    }

    def "should allow defining EntityType in JaversBuilder"(){
        given:
        def javers = JaversBuilder.javers()
                .registerEntity(EntityDefinitionBuilder.entityDefinition(DataSource).withIdPropertyName("name").build())
                .build()

        expect:
        javers.getTypeMapping(DataSource).idProperty.name == "name"
        javers.getTypeMapping(DataSource).properties.size() == 2
    }
}
