package org.javers.core.prettyprint

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Person
import org.javers.core.metamodel.property.Property
import org.javers.core.metamodel.type.ManagedType
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class TypeMappingPrintDemo extends Specification {

    def "should pretty print JaVers types"() {
        expect:
        def javers = JaversBuilder.javers().build()

        def t = javers.getTypeMapping(DummyUserDetails)

        println "toString: "+  t.toString()
        println "pretty: " + t.prettyPrint()

        true
    }

    //Java style is deliberated
    def "should allow basic Reflective operations"() {
        expect:
        Javers javers = JaversBuilder.javers().build();
        ManagedType jType = javers.getTypeMapping(Person.class);
        Person person = new Person("bob", "Uncle Bob");

        System.out.println("Bob's properties:");
        for (Property p : jType.getManagedClass().getProperties()){
            Object value = p.get(person);
            System.out.println( "property:"+ p.getName() +", value:"+value );
        }

        true
    }
}
