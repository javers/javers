package org.javers.core.cases

import jakarta.persistence.Id
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.type.EntityType
import spock.lang.Specification

/**
 * @author khan
 */
class CaseInheritanceWithIgnoredField extends Specification {
    @TypeName("A")
    class A {
        @Id
        String id
        @DiffIgnore
        String field
    }
    @TypeName("B")
    class B extends A {
        @DiffIgnore
        String someOtherField
    }
    @TypeName("C")
    class C extends A {
        @DiffIgnore
        String someOtherField
    }

    def "should ignore fields on sub Class that are annotated with DiffIgnore"(){
        given:
        def javers = JaversBuilder.javers().build()

        when:
        EntityType bType = javers.getTypeMapping(B)
        // get typeMapping of the parent class
        EntityType aType = javers.getTypeMapping(A)
        EntityType cType = javers.getTypeMapping(C)

        then:
        println aType.prettyPrint()
        println bType.prettyPrint()
        println cType.prettyPrint()

        assert bType.findProperty("someOtherField").isEmpty()
        assert bType.findProperty("field").isEmpty()
        assert cType.findProperty("someOtherField").isEmpty()
        assert cType.findProperty("field").isEmpty()
    }

}
